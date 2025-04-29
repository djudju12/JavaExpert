package org.javaexpert.expert;

import org.javaexpert.expert.attribute.Attribute;
import org.javaexpert.expert.attribute.NumericAttribute;
import org.javaexpert.expert.attribute.StringAttribute;
import org.javaexpert.expert.fact.Fact;
import org.javaexpert.expert.fact.NumericFact;
import org.javaexpert.expert.fact.StringFact;
import org.javaexpert.expert.predicate.CompoundPredicate;
import org.javaexpert.expert.predicate.Predicate;
import org.javaexpert.expert.predicate.SimplePredicate;
import org.javaexpert.parser.Parser;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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

public class Expert {
    private final Map<String, Rule> rules;
    private final Map<String, Attribute> attributes;
    private final Set<String> objectives;
    private final List<Rule> conclusiveRules;
    private final Map<String, Fact> facts = new TreeMap<>();
    private final StringBuilder log = new StringBuilder();
    private final TreeLogger tree;
    private String question = null;
    private final String system;
    private final Set<String> askableAttrs = new HashSet<>();

    public Expert(Map<String, Attribute> attrs, Map<String, Rule> rules, Set<String> objectives, String system) {
        this.rules = rules;
        this.attributes = attrs;
        this.objectives = objectives;
        this.conclusiveRules = conclusiveRules(rules.values(), objectives);
        this.system = system;
        tree = new TreeLogger();
    }

    public static Expert fromFile(String filePath) throws IOException {
        return new Parser(filePath).parse();
    }

    public Set<Fact> getFacts() {
        return new TreeSet<>(facts.values());
    }

    public Map<String, Fact> getObjectivesConclusions() {
        Map<String, Fact> m = HashMap.newHashMap(objectives.size());
        objectives.forEach(o -> m.put(o, facts.get(o)));
        return m;
    }

    public List<String> getAttributesValues(String attrName) {
        if (attributes.get(attrName) instanceof StringAttribute strAttr) return strAttr.getOrderedValues();
        throw new IllegalStateException("string attribute %s not found".formatted(attrName));
    }

    public void addAskable(String attr) {
        askableAttrs.add(attr);
    }

    private void clearLog() {
        tree.clear();
        log.setLength(0);
    }

    public void clearMemory() {
        facts.clear();
    }

    private void clearNotAskableFacts() {
        facts.keySet().removeIf(k -> !askableAttrs.contains(k));
    }

    public Optional<String> thinkIfNotConclusiveAskQuestion() {
        // devido ao fato deste metodo ser invocado `n` vezes, ate encontrar um resultado, precisamos recalcular em cada
        // chamada as regras intermediarias, pois os valores usados para valida-las podem ter sido alterados
        clearNotAskableFacts();

        clearLog();
        for (var rule: conclusiveRules) {
            if (verifyRule(rule, new TreeSet<>(rules.values()), null)) {
                log.append(tree.print());
                log.append(format("%n>>>>> REGRA ACEITA: '%s' <<<<<%n", rule.name()));
                return Optional.empty();
            }

            if (question != null) {
                log.append(tree.print());
                log.append("\n>>>>> NENHUMA REGRA ENCONTRADA <<<<<\n");
                var ret = Optional.of(question);
                question = null;
                return ret;
            }
        }

        log.append(tree.print());
        log.append("\n>>>>> NENHUMA REGRA ENCONTRADA <<<<<\n");

        return Optional.empty();
    }

    public Optional<Rule> think() {
        tree.clear();
        log.setLength(0);
        return conclusiveRules
                .stream()
                .filter(rule -> {
                    if (verifyRule(rule, new TreeSet<>(rules.values()), null)) {
                        log.append(tree.print());
                        log.append(format("%n>>>>> REGRA ACEITA: '%s' <<<<<%n", rule.name()));
                        return true;
                    }

                    return false;
                })
                .findFirst();
    }

    private boolean verifyPredicate(Predicate predicate, Set<Rule> rules, TreeLogger.Node parent) {
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

        if (question == null && askableAttrs.contains(simple.name())) {
            question = simple.name();
        }

        return false;
    }

    private boolean searchFactInRules(SimplePredicate simple, Set<Rule> rules, TreeLogger.Node parent) {
        for (var rule: rules) {
            var isAboutPredicate = rule.conclusions()
                    .stream()
                    .anyMatch(f -> f.name().equals(simple.name()));

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
                if (f instanceof StringFact stringFact) {
                    stringFact.value().forEach(v -> tree.appendf(then, "'%s' := '%s'", f.name(), v));
                } else {
                    tree.appendf(then, "'%s' := '%s'", f.name(), f.value());
                }
                facts.putIfAbsent(f.name(), f);
            });

        } else {
            tree.appendf(child, "REGRA '%s' NÃO APLICADA", rule.name());
        }

        return isRuleTrue;
    }

    public String print() {
        return log.toString();
    }

    public void newFact(String attrName, Object value) {
        var attr = attributes.get(attrName);
        assertNotNull(attr, "attribute '%s' not found".formatted(attrName));
        if (attr instanceof NumericAttribute) {
            facts.put(attrName, new NumericFact(attrName, (int) value));
        } else if (attr instanceof StringAttribute strAttr) {
            assertTrue(strAttr.contains((String) value), "invalid fact. '%s' is not a valid value for '%s'".formatted(value, attrName));
            var strFact = (StringFact) facts.computeIfAbsent(attrName, k -> new StringFact(k, new HashSet<>()));
            strFact.value().add((String) value);
        } else {
            throw new IllegalStateException("invalid fact. Attribute '%s' is not a number or string".formatted(attrName));
        }
    }

    public void removeFact(String attrName) {
        facts.remove(attrName);
    }

    private static List<Rule> conclusiveRules(Collection<Rule> rules, Set<String> objectives) {
        return rules
            .stream()
            .filter(rule -> {
                var conclusionNames = rule.conclusions().stream().map(Fact::name).collect(Collectors.toSet());
                return conclusionNames.stream().anyMatch(objectives::contains);
            })
            .toList();
    }

    public String getSystem() {
        return system;
    }
}
