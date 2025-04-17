package org.javaexpert.expert;

import org.javaexpert.TreeLogger;
import org.javaexpert.expert.fact.Fact;
import org.javaexpert.expert.predicate.Predicate;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record Rule(String name, Predicate predicate, Set<Fact<?>> conclusions) implements Comparable<Rule> {
    private final static TreeLogger logger = TreeLogger.instance();

    public boolean isTrue(Set<Rule> rules, Map<String, Fact<?>> facts) {
        return isTrue(rules, facts, null);
    }

    public boolean isTrue(Set<Rule> rules, Map<String, Fact<?>> facts, TreeLogger.Node parent) {
        var child = logger.appendf(parent, "REGRA '%s':", name());
        var otherRules = rules.stream().filter(rule -> !rule.equals(this)).collect(Collectors.toSet());
        var isRuleTrue =  predicate.isTrue(otherRules, facts, child);
        if (isRuleTrue) {
//            logger.appendf(child, "RESULTADO: VERDADEIRA");
            var then = logger.appendf(child, "REGRA '%s' APLICADA", name());
            conclusions().forEach(f -> {
                logger.appendf(then, "'%s' := '%s'", f.getName(), f.getValue());
                facts.putIfAbsent(f.getName(), f);
            });

        } else {
            logger.appendf(child, "REGRA '%s' NÃO APLICADA", name());
        }

        return isRuleTrue;
    }

    @Override
    public int compareTo(Rule o) {
        return o.name().compareTo(name());
    }
}
