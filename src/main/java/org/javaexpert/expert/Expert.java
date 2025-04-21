package org.javaexpert.expert;

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
import org.javaexpert.expert.predicate.SimplePredicate;
import org.javaexpert.expert.predicate.StringPredicate;
import org.javaexpert.lexer.Lexer;
import org.javaexpert.lexer.Token;
import org.javaexpert.lexer.TokenLogicOperator;
import org.javaexpert.lexer.TokenNum;
import org.javaexpert.lexer.TokenStr;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.javaexpert.Asserts.assertNotNull;
import static org.javaexpert.Asserts.assertTrue;
import static org.javaexpert.expert.predicate.LogicConnector.AND;
import static org.javaexpert.expert.predicate.LogicConnector.OR;

public class Expert {
    private final Map<String, Rule> rules;
    private final Map<String, Attribute> attributes;
    private final Set<String> objectives;
    private final Map<String, Fact<?>> facts = new TreeMap<>();
    private final StringBuilder log = new StringBuilder();
    private final TreeLogger tree;

    protected Expert(Map<String, Attribute> attrs, Map<String, Rule> rules, Set<String> objectives) {
        this.rules = rules;
        this.attributes = attrs;
        this.objectives = objectives;
        tree = new TreeLogger();
    }

    public static Expert fromFile(String filePath) throws IOException {
        return new Parser(filePath).parse();
    }

    public Set<String> getObjectives() {
        return objectives;
    }

    public Collection<Fact<?>> getFacts() {
        return facts.values();
    }

    public Set<String> getAttributesValues(String attrName) {
        if (attributes.get(attrName) instanceof StringAttribute strAttr) return strAttr.values();
        throw new IllegalStateException("string attribute %s not found".formatted(attrName));
    }

    public void clearMemory() {
        facts.clear();
        log.setLength(0);
    }

    public Optional<Rule> think() {
        var tree = new TreeLogger();
        return conclusiveRules()
                .stream()
                .filter(rule -> {
                    if (verifyRule(rule, new TreeSet<>(rules.values()), null)) {
                        log.append(tree.print());
                        log.append(format("\n>>>>> REGRA ACEITA: '%s' <<<<<\n", rule.name()));
                        return true;
                    }

                    return false;
                })
                .findFirst();
    }

    public boolean verifyPredicate(Predicate predicate, Set<Rule> rules, TreeLogger.Node parent) {
        return switch (predicate) {
            case CompoundPredicate compound -> verifyCompoundPredicate(compound, rules, parent);
            case SimplePredicate simple -> {
                var isTrue = verifySimplePredicate(simple, rules, parent);
                tree.appendf(parent, "'%s' %s '%s'? %s", simple.name(), simple.operator(), simple.value(),
                        isTrue ? "~>[VERDADEIRO]" : "~>[FALSO]");
                yield isTrue;
            }
        };
    }

    private boolean verifySimplePredicate(SimplePredicate simple, Set<Rule> rules, TreeLogger.Node parent) {
        var fact = facts.get(simple.name());
        if (fact != null) {
            return simple.validateFact(fact);
        }

        var child = tree.appendf(parent, "PROCURANDO '%s'...", simple.name());
        if (searchFactInRules(simple, rules, child)) {
            return simple.validateFact(facts.get(simple.name()));
        }

        tree.appendf(parent, "NÃO ENCONTROU '%s'!", simple.name());

        // ask question

        return simple.validateFact(facts.get(simple.name()));
    }

    private boolean searchFactInRules(SimplePredicate simple, Set<Rule> rules, TreeLogger.Node parent) {
        for (var rule: rules) {
            var isAboutPredicate = rule.conclusions()
                    .stream()
                    .anyMatch(f -> f.getName().equals(simple.name()));

            if (isAboutPredicate && verifyRule(rule, rules, parent)) {
                return true;
            }
        }

        return false;
    }

    private boolean verifyCompoundPredicate(CompoundPredicate compound, Set<Rule> rules, TreeLogger.Node parent) {
        var a = verifyPredicate(compound.lhs(), rules, parent);
        return switch (compound.connector()) {
            case AND -> (a && tree.appendf(parent, "%s...", compound.connector()) != null) && verifyPredicate(compound.rhs(), rules, parent);
            case OR -> {
                if (!a) {
                    tree.appendf(parent, "%s...", compound.connector());
                    yield verifyPredicate(compound.rhs(), rules, parent);
                }
                yield true;
            }
        };
    }

    public boolean verifyRule(Rule rule, Set<Rule> notCheckedRules, TreeLogger.Node parent) {
        var child = tree.appendf(parent, "ENTRANDO NA REGRA '%s':", rule.name());
        var otherRules = notCheckedRules.stream().filter(other -> !other.equals(rule)).collect(Collectors.toSet());
        var isRuleTrue =  verifyPredicate(rule.predicate(), otherRules, child);
        if (isRuleTrue) {
            var then = tree.appendf(child, "REGRA '%s' APLICADA", rule.name());
            rule.conclusions().forEach(f -> {
                tree.appendf(then, "'%s' := '%s'", f.getName(), f.getValue());
                facts.putIfAbsent(f.getName(), f);
            });

        } else {
            tree.appendf(child, "REGRA '%s' NÃO APLICADA", rule.name());
        }

        return isRuleTrue;
    }

    public String print() {
        return log.toString();
    }

    public <T> void newFact(String attrName, T value) {
        var attr = attributes.get(attrName);
        assertNotNull(attr, "attribute '%s' not found".formatted(attrName));
        if (attr instanceof NumericAttribute) {
            facts.put(attrName, new NumericFact(attrName, (int) value));
        } else if (attr instanceof StringAttribute strAttr) {
            assertTrue(strAttr.values().contains((String) value), "invalid fact. '%s' is not a valid value for '%s'".formatted(value, attrName));
            facts.put(attrName, new StringFact(attrName, (String) value));
        } else {
            throw new IllegalStateException("invalid fact. Attribute '%s' is not a number or string".formatted(attrName));
        }
    }

    public void removeFact(String attrName) {
        facts.remove(attrName);
    }

    private List<Rule> conclusiveRules() {
        return rules.values()
            .stream()
            .filter(rule -> {
                var conclusionNames = rule.conclusions().stream().map(Fact::getName).collect(Collectors.toSet());
                return conclusionNames.stream().anyMatch(objectives::contains);
            })
            .toList();
    }

    private static class Parser {

        private final Map<String, Rule> rules = new TreeMap<>();
        private final Map<String, Attribute> attrs = new TreeMap<>();
        private final Set<String> objectives = new TreeSet<>();

        private final Lexer lexer;

        private Parser(String filePath) throws IOException {
            this.lexer = new Lexer(filePath);
        }

        protected Expert parse() {
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

}
