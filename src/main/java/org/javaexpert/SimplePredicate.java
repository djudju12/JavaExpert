package org.javaexpert;

import java.util.Map;
import java.util.Set;

public record SimplePredicate (Fact predicate) implements Predicate {

    @Override
    public PredicateType type() {
        return PredicateType.SIMPLE;
    }

    @Override
    public boolean evaluate(Set<Rule> rules, Map<String, Fact> facts) {
        var fact = facts.get(predicate.getName());
        if (fact != null) {
            return fact.isTrue(predicate);
        }

        for (var rule : rules) {
            for (var conclusion: rule.getConclusions()) {
                if (predicate.isTrue(conclusion) && rule.evaluate(rules, facts)) {
                    facts.put(predicate().getName(), predicate);
                    return true;
                }
            }
        }

        return false;
    }
}
