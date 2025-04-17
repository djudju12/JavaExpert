package org.javaexpert.expert;

import org.javaexpert.TreeLogger;
import org.javaexpert.expert.fact.Fact;
import org.javaexpert.expert.predicate.Predicate;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record Rule(String name, Predicate predicate, Set<Fact<?>> conclusions) implements Comparable<Rule> {

    public boolean isTrue(Set<Rule> rules, Map<String, Fact<?>> facts, TreeLogger tree) {
        return isTrue(rules, facts, tree, null);
    }

    public boolean isTrue(Set<Rule> rules, Map<String, Fact<?>> facts, TreeLogger tree, TreeLogger.Node parent) {
        var child = tree.appendf(parent, "ENTRANDO NA REGRA '%s':", name());
        var otherRules = rules.stream().filter(rule -> !rule.equals(this)).collect(Collectors.toSet());
        var isRuleTrue =  predicate.isTrue(otherRules, facts, tree, child);
        if (isRuleTrue) {
//            logger.appendf(child, "RESULTADO: VERDADEIRA");
            var then = tree.appendf(child, "REGRA '%s' APLICADA", name());
            conclusions().forEach(f -> {
                tree.appendf(then, "'%s' := '%s'", f.getName(), f.getValue());
                facts.putIfAbsent(f.getName(), f);
            });

        } else {
            tree.appendf(child, "REGRA '%s' NÃO APLICADA", name());
        }

        return isRuleTrue;
    }

    @Override
    public int compareTo(Rule o) {
        return o.name().compareTo(name());
    }
}
