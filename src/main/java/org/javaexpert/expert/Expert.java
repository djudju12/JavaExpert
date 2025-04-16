package org.javaexpert.expert;

import org.javaexpert.lexer.Lexer;
import org.javaexpert.lexer.LogicOperator;
import org.javaexpert.lexer.Token;
import org.javaexpert.lexer.TokenLogicOperator;
import org.javaexpert.lexer.TokenNum;
import org.javaexpert.lexer.TokenStr;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.javaexpert.Asserts.assertTrue;
import static org.javaexpert.expert.LogicConnector.AND;
import static org.javaexpert.expert.LogicConnector.OR;

public class Expert {
    private final Map<String, Rule> rules;
    private final Map<String, Attribute> attributes;
    private final Set<String> objectives;

    protected Expert(Map<String, Attribute> attrs, Map<String, Rule> rules, Set<String> objectives) {
        this.rules = rules;
        this.attributes = attrs;
        this.objectives = objectives;
    }

    public static Expert fromFile(String filePath) throws IOException {
        return Parser.parse(filePath);
    }

    public Set<Fact<?>> thinkAboutFacts(Map<String, Fact<?>> facts) {
        return conclusiveRules()
                .stream()
                .filter(rule -> rule.isTrue(new HashSet<>(rules.values()), facts))
                .findFirst()
                .map(Rule::conclusions)
                .orElse(Set.of());
    }

    private Set<Rule> conclusiveRules() {
        return rules.values()
            .stream()
            .filter(rule -> {
                var conclusionNames = rule.conclusions().stream().map(Fact::getName).collect(Collectors.toSet());
                return conclusionNames.stream().anyMatch(objectives::contains);
            })
            .collect(Collectors.toSet());
    }

    private static class Parser {

        private Parser() { }

        protected static Expert parse(String filePath) throws IOException {
            var lexer = new Lexer(filePath);
            var rules = new HashMap<String, Rule>();
            var attrs = new HashMap<String, Attribute>();
            var objectives = new HashSet<String>();
            for (var optToken = lexer.nextToken(); optToken.isPresent(); optToken = lexer.nextToken()) {
                var token = optToken.get();
                switch (token.getType()) {
                    case RULE -> {
                        var rule = parseRule(lexer);
                        rules.put(rule.name(), rule);
                    }

                    case ATTRIBUTE -> {
                        var attr = parseAttr(lexer);
                        attrs.put(attr.name(), attr);
                    }

                    case OBJECTIVES -> objectives.addAll(parseObjectives(lexer));

                    default -> throw new RuntimeException("%s: unexpected token %s".formatted(token.getLocation(), token));
                }
            }

            return new Expert(attrs, rules, objectives);
        }

        private static Set<String> parseObjectives(Lexer lexer) {
            var objs = new HashSet<String>();
            var token = lexer.requireNextToken(Token.TokenType.OPEN_PAR);
            do {
                token = lexer.requireNextToken(Token.TokenType.STR);
                objs.add(((TokenStr) token).getValue());
                token = lexer.requireNextToken();
            } while (token.getType() == Token.TokenType.COMMA);

            return objs;
        }

        private static Rule parseRule(Lexer lexer) {
            var token = lexer.requireNextToken(Token.TokenType.STR);
            var name = ((TokenStr) token).getValue();

            lexer.requireNextToken(Token.TokenType.OPEN_PAR);
            lexer.requireNextToken(Token.TokenType.SE);
            var predicate = parseSe(lexer);
            var conclusions = parseEntao(lexer);
            return new Rule(name, predicate, conclusions);
        }

        private static Attribute parseAttr(Lexer lexer) {
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

        private static Set<Fact<?>> parseEntao(Lexer lexer) {
            var conclusions = new HashSet<Fact<?>>();
            Token token;
            do {
                conclusions.add(parseFact(lexer));
                token = lexer.requireNextToken();
            } while (token.getType() == Token.TokenType.E);

            assertTrue(token.getType() == Token.TokenType.CLOSE_PAR, "expected ')'", token.getLocation());
            return conclusions;
        }

        private static Predicate parseSe(Lexer lexer) {
            var predicate = parseSimplePredicate(lexer);

            do {
                var token = lexer.requireNextToken();
                switch (token.getType()) {
                    case E -> {
                        var otherPredicate = parseSimplePredicate(lexer);
                        predicate = new CompoundPredicate(predicate, otherPredicate, AND);
                    }
                    case OU -> {
                        var otherPredicate = parseSimplePredicate(lexer);
                        predicate = new CompoundPredicate(predicate, otherPredicate, OR);
                    }
                    case ENTAO -> {
                        return predicate;
                    }
                    default -> throw new RuntimeException("%s: unexpected token %s".formatted(token.getLocation(), token));
                }
            } while (true);
        }

        private static Predicate parseSimplePredicate(Lexer lexer) {
            var token = lexer.requireNextToken();
            assertTrue(token.getType() == Token.TokenType.STR, "expected string", token.getLocation());
            var attributeName = ((TokenStr) token).getValue();

            var optoken = (TokenLogicOperator) lexer.requireNextToken(Token.TokenType.LOGIC_OPERATOR);
            var op = optoken.getValue();
            token = lexer.requireNextToken();
            return switch (token) {
                case TokenStr str -> {
                    assertTrue(op == LogicOperator.EQ, "invalid operator between string", optoken.getLocation());
                    yield new StringPredicate(attributeName, str.getValue(), op);
                }
                case TokenNum num -> new NumericPredicate(attributeName, num.getValue(), op);
                default -> throw new RuntimeException("%s: unexpected token %s".formatted(token.getLocation(), token));
            };
        }

        private static Fact<?> parseFact(Lexer lexer) {
            var token = lexer.requireNextToken();
            assertTrue(token.getType() == Token.TokenType.STR, "expected string", token.getLocation());
            var attributeName = ((TokenStr) token).getValue();

            var optoken = (TokenLogicOperator) lexer.requireNextToken(Token.TokenType.LOGIC_OPERATOR);
            assertTrue(optoken.getValue() == LogicOperator.EQ, "invalid fact operator. Must be a '='", optoken.getLocation());
            token = lexer.requireNextToken();
            return switch (token) {
                case TokenStr str -> new StringFact(attributeName, str.getValue());
                case TokenNum num -> new NumericFact(attributeName, num.getValue());
                default -> throw new RuntimeException("%s: unexpected token %s".formatted(token.getLocation(), token));
            };
        }
    }


}
