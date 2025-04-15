package org.javaexpert;

import java.util.Map;
import java.util.Set;

public record SimplePredicate (Fact predicate) implements Predicate {

    @Override
    public PredicateType type() {
        return PredicateType.SIMPLE;
    }

    @Override
    public boolean isAbout(String attribute) {
        return predicate().name().equals(attribute);
    }

    @Override
    public boolean evaluate(Set<Rule> rules, Map<String, String> facts) {
        var fact = facts.get(predicate.name());
        if (fact != null) {
            return fact.equals(predicate.value());
        }

        for (var rule : rules) {
            for (var conclusion: rule.getConclusions()) {
                if (conclusion.equals(predicate) && rule.evaluate(rules, facts)) {
                    facts.put(predicate().name(), predicate.value());
                    return true;
                }
            }
        }

        return false;
    }
}
