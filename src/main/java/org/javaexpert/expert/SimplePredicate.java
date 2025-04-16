package org.javaexpert.expert;

import java.util.Map;
import java.util.Set;

public record SimplePredicate (Fact predicate) implements Predicate {

    @Override
    public PredicateType type() {
        return PredicateType.SIMPLE;
    }

    @Override
    public boolean isTrue(Set<Rule> rules, Map<String, Fact> facts) {
        var fact = facts.get(predicate.getName());
        if (fact != null) {
            return predicate.equals(fact);
        }

        for (var rule : rules) {
            for (var conclusion: rule.conclusions()) {
                if (predicate.equals(conclusion) && rule.isTrue(rules, facts)) {
                    facts.put(predicate().getName(), predicate);
                    return true;
                }
            }
        }

        return false;
    }
}
