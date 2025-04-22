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
import static org.javaexpert.expert.predicate.LogicConnector.AND;
import static org.javaexpert.expert.predicate.LogicConnector.OR;

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
            switch (token.getType()) {
                case RULE -> {
                    var rule = parseRule();
                    rules.put(rule.name(), rule);
                }

                case ATTRIBUTE -> {
                    var attr = parseAttr();
                    attrs.put(attr.name(), attr);
                }

                case OBJECTIVES -> objectives.addAll(parseObjectives());

                default -> throw new RuntimeException("%s: unexpected token %s".formatted(token.getLocation(), token));
            }
        }

        return new Expert(attrs, rules, objectives);
    }

    private Set<String> parseObjectives() {
        var objs = new TreeSet<String>();
        var token = lexer.requireNextToken(Token.TokenType.OPEN_PAR);
        do {
            token = lexer.requireNextToken(Token.TokenType.STR);
            objs.add(((TokenStr) token).getValue());
            token = lexer.requireNextToken();
        } while (token.getType() == Token.TokenType.COMMA);

        return objs;
    }

    private Rule parseRule() {
        var token = lexer.requireNextToken(Token.TokenType.STR);
        var name = ((TokenStr) token).getValue();

        lexer.requireNextToken(Token.TokenType.OPEN_PAR);
        lexer.requireNextToken(Token.TokenType.SE);
        var predicate = parseSe();
        var conclusions = parseEntao();
        return new Rule(name, predicate, conclusions);
    }

    private Attribute parseAttr() {
        var token = lexer.requireNextToken(Token.TokenType.STR);
        var name = ((TokenStr) token).getValue();
        token = lexer.requireNextToken();

        if (token.getType() == Token.TokenType.OPEN_PAR) {
            var attr = new StringAttribute(name);
            do {
                token = lexer.requireNextToken(Token.TokenType.STR);
                attr.values().add(((TokenStr) token).getValue());
                token = lexer.requireNextToken();
            } while (token.getType() == Token.TokenType.COMMA);

            assertTrue(token.getType() == Token.TokenType.CLOSE_PAR, "%s: expected '%s' found: '%s' ".formatted(token.getLocation(), Token.TokenType.CLOSE_PAR, token.getType()));
            return attr;
        } else if (token.getType() == Token.TokenType.ATTR_NUMERIC) {
            return new NumericAttribute(name);
        }

        throw new RuntimeException("%s: unexpected token %s".formatted(token.getLocation(), token));
    }

    private Set<Fact<?>> parseEntao() {
        var conclusions = new TreeSet<Fact<?>>();
        Token token;
        do {
            conclusions.add(parseFact());
            token = lexer.requireNextToken();
        } while (token.getType() == Token.TokenType.E);

        assertTrue(token.getType() == Token.TokenType.CLOSE_PAR, "expected ')'", token.getLocation());
        return conclusions;
    }

    private Predicate parseSe() {
        var predicate = parseSimplePredicate();

        do {
            var token = lexer.requireNextToken();
            switch (token.getType()) {
                case E -> {
                    var otherPredicate = parseSimplePredicate();
                    predicate = new CompoundPredicate(predicate, otherPredicate, AND);
                }
                case OU -> {
                    var otherPredicate = parseSimplePredicate();
                    predicate = new CompoundPredicate(predicate, otherPredicate, OR);
                }
                case ENTAO -> {
                    return predicate;
                }
                default -> throw new RuntimeException("%s: unexpected token %s".formatted(token.getLocation(), token));
            }
        } while (true);
    }

    private Predicate parseSimplePredicate() {
        var attributeName = parseAttrName();
        var attr = attrs.get(attributeName);
        var optoken = (TokenLogicOperator) lexer.requireNextToken(Token.TokenType.LOGIC_OPERATOR);
        var op = optoken.getValue();
        return switch (lexer.requireNextToken()) {
            case TokenStr str -> {
                assertTrue(op == LogicOperator.EQ, "invalid operator between string", optoken.getLocation());
                assertTrue(attr instanceof StringAttribute, "invalid predicate. Attribute is not string", str.getLocation());
                yield new StringPredicate(attributeName, str.getValue(), op);
            }
            case TokenNum num -> {
                assertTrue(attr instanceof NumericAttribute, "invalid predicate. Attribute is not a number", num.getLocation());
                yield new NumericPredicate(attributeName, num.getValue(), op);
            }
            case Token token -> throw new RuntimeException("%s: unexpected token %s".formatted(token.getLocation(), token));
        };
    }

    private Fact<?> parseFact() {
        var attributeName = parseAttrName();
        var optoken = (TokenLogicOperator) lexer.requireNextToken(Token.TokenType.LOGIC_OPERATOR);
        assertTrue(optoken.getValue() == LogicOperator.EQ, "invalid fact operator. Must be a '='", optoken.getLocation());
        return switch (lexer.requireNextToken()) {
            case TokenStr str -> new StringFact(attributeName, str.getValue());
            case TokenNum num -> new NumericFact(attributeName, num.getValue());
            case Token token -> throw new RuntimeException("%s: unexpected token %s".formatted(token.getLocation(), token));
        };
    }

    private String parseAttrName() {
        var token = lexer.requireNextToken(Token.TokenType.STR);
        var attributeName = ((TokenStr) token).getValue();
        var attr = attrs.get(attributeName);
        assertNotNull(attr, "attribute not found", token.getLocation());
        return attributeName;
    }
}