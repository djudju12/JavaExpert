package org.javaexpert.parser;

import org.javaexpert.expert.Expert;
import org.javaexpert.expert.Rule;
import org.javaexpert.expert.attribute.Attribute;
import org.javaexpert.expert.attribute.NumericAttribute;
import org.javaexpert.expert.attribute.StringAttribute;
import org.javaexpert.expert.fact.Fact;
import org.javaexpert.expert.fact.NumericFact;
import org.javaexpert.expert.fact.StringFact;
import org.javaexpert.expert.predicate.CompoundPredicate;
import org.javaexpert.expert.predicate.LogicOperator;
import org.javaexpert.expert.predicate.NumericPredicate;
import org.javaexpert.expert.predicate.Predicate;
import org.javaexpert.expert.predicate.StringPredicate;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static org.javaexpert.Asserts.assertNotNull;
import static org.javaexpert.Asserts.assertTrue;
import static org.javaexpert.parser.Token.TokenType.CLOSE_PAR;
import static org.javaexpert.parser.Token.TokenType.COMMA;
import static org.javaexpert.parser.Token.TokenType.ENTAO;
import static org.javaexpert.parser.Token.TokenType.LOGIC_CONNECTOR;
import static org.javaexpert.parser.Token.TokenType.LOGIC_OPERATOR;
import static org.javaexpert.parser.Token.TokenType.OPEN_PAR;
import static org.javaexpert.parser.Token.TokenType.SE;
import static org.javaexpert.parser.Token.TokenType.STR;

public class Parser {

    private final Map<String, Rule> rules = new TreeMap<>();
    private final Map<String, Attribute> attrs = new TreeMap<>();
    private final Set<String> objectives = new TreeSet<>();

    private final Lexer lexer;

    public Parser(String filePath) throws IOException {
        this.lexer = new Lexer(filePath);
    }

    public Expert parse() {
        for (var optToken = lexer.nextToken(); optToken.isPresent(); optToken = lexer.nextToken()) {
            var token = optToken.get();
            switch (token.type()) {
                case RULE: {
                    var rule = parseRule();
                    rules.put(rule.name(), rule);
                } break;

                case ATTRIBUTE: {
                    var attr = parseAttr();
                    attrs.put(attr.name(), attr);
                } break;

                case OBJECTIVES: objectives.addAll(parseObjectives()); break;

                default: throw new UnexpectedTokenException(token);
            }
        }

        return new Expert(attrs, rules, objectives, lexer.getContent());
    }

    private Set<String> parseObjectives() {
        var objs = new TreeSet<String>();
        var token = lexer.requireNextToken(OPEN_PAR);
        do {
            token = lexer.requireNextToken(STR);
            objs.add(token.valueStr());
        } while (lexer.requireNextToken().type() == COMMA);

        lexer.assertLastToken(CLOSE_PAR);

        return objs;
    }

    private Rule parseRule() {
        var token = lexer.requireNextToken(STR);
        var name = token.valueStr();

        lexer.requireNextToken(OPEN_PAR);
        lexer.requireNextToken(SE);
        var predicate = parseSe();
        var conclusions = parseEntao();
        return new Rule(name, predicate, conclusions);
    }

    private Attribute parseAttr() {
        var token = lexer.requireNextToken(STR);
        var name = token.valueStr();
        token = lexer.requireNextToken();

        return switch (token.type()) {
            case ATTR_NUMERIC -> new NumericAttribute(name);
            case ATTR_STRING -> { // STRINGS
                lexer.requireNextToken(OPEN_PAR);
                var attr = new StringAttribute(name);
                do {
                    token = lexer.requireNextToken(STR);
                    attr.addValue(token.valueStr());
                } while (lexer.requireNextToken().type() == COMMA);

                lexer.assertLastToken(CLOSE_PAR);
                yield attr;
            }

            default -> throw new UnexpectedTokenException(token);
        };
    }

    private Set<Fact> parseEntao() {
        var conclusions = new TreeSet<Fact>();

        do {
            var fact = parseFact();
            if (fact instanceof StringFact sf) {
                conclusions.stream()
                        .filter(StringFact.class::isInstance)
                        .filter(f -> f.name().equals(fact.name()))
                        .findFirst()
                        .ifPresentOrElse(
                            f -> ((StringFact) f).value().addAll(sf.value()),
                            () -> conclusions.add(fact)
                        );
            } else {
                conclusions.add(fact);
            }
        } while (lexer.requireNextToken().isAND());

        lexer.assertLastToken(CLOSE_PAR);
        return conclusions;
    }

    private Predicate parseSe() {
        var predicate = parsePredicate();
        lexer.assertLastToken(ENTAO);
        return predicate;
    }

    private Predicate parsePredicate() {
        Predicate predicate;
        var token = lexer.requireNextToken();
        if (token.type() == OPEN_PAR) {
            predicate = parsePredicate();
            lexer.assertLastToken(CLOSE_PAR);
        } else {
            predicate = parseSimplePredicate();
        }

        do {
            token = lexer.requireNextToken();
            if (token.type() == LOGIC_CONNECTOR) {
                predicate = new CompoundPredicate(predicate, parsePredicate(), token.valueLogicConn());
            }
        } while (lexer.getLastToken().type() == LOGIC_CONNECTOR);

        return predicate;
    }

    private Predicate parseSimplePredicate() {
        var attributeName = parseAttrName();
        var attr = attrs.get(attributeName);
        assertNotNull(attr, "attribute '%s' not found".formatted(attributeName), lexer.getLastToken().location());
        var optoken = lexer.requireNextToken(LOGIC_OPERATOR);
        var op = optoken.valueLogicOp();
        var token = lexer.requireNextToken();
        return switch (token.type()) {
            case STR -> {
                assertTrue(op == LogicOperator.EQ || op == LogicOperator.NEQ, "invalid operator between string", optoken.location());
                assertTrue(attr instanceof StringAttribute, "invalid predicate. Attribute is not string", token.location());
                yield new StringPredicate(attributeName, token.valueStr(), op);
            }

            case NUM -> {
                assertTrue(attr instanceof NumericAttribute, "invalid predicate. Attribute is not a number", token.location());
                yield new NumericPredicate(attributeName, token.valueInt(), op);
            }

            default -> throw new UnexpectedTokenException(token);
        };
    }

    private Fact parseFact() {
        lexer.requireNextToken(STR);
        var attributeName = parseAttrName();
        var token = lexer.requireNextToken(LOGIC_OPERATOR);
        assertTrue(token.valueLogicOp() == LogicOperator.EQ, "invalid fact operator. Must be a '='", token.location());
        var attr = attrs.get(attributeName);
        assertNotNull(attr, "attribute '%s' not found".formatted(attributeName), lexer.getLastToken().location());
        token = lexer.requireNextToken();

        return switch (token.type()) {
            case STR -> {
                if (attr instanceof StringAttribute stringAttribute) {
                    assertTrue(
                        stringAttribute.contains(token.valueStr()),
                        "attribute '%s' does not contains value '%s'".formatted(attributeName, token.valueStr()),
                        token.location()
                    );
                    yield new StringFact(attributeName, token.valueStr());
                }
                throw new UnexpectedTokenException(token);
            }
            case NUM -> new NumericFact(attributeName, token.valueInt());
            default -> throw new UnexpectedTokenException(token);
        };
    }

    private String parseAttrName() {
        lexer.assertLastToken(STR);
        var token = lexer.getLastToken();
        return token.valueStr();
//        var attr = attrs.get(attributeName);
//        assertNotNull(attr, "attribute '%s' not found".formatted(attributeName), token.location());
//        return attributeName;
    }
}