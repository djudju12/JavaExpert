package org.javaexpert.expert;

import java.util.Map;
import java.util.Set;

public sealed interface Predicate permits NumericPredicate, StringPredicate, CompoundPredicate {

    PredicateType type();

    String name();

    enum PredicateType {
        SIMPLE, COMPOUND
    }

    default boolean isTrue(Set<Rule> rules, Map<String, Fact<?>> facts) {
        var fact = facts.get(name());
        if (fact != null) {
            return validateFact(fact);
        }

        for (var rule: rules) {
            var isAboutPredicate = rule.conclusions().stream().anyMatch(f -> f.getName().equals(name()));
            if (isAboutPredicate && rule.isTrue(rules, facts) && validateFact(facts.get(name()))) {
                return true;
            }
        }

        return false;
    }

    boolean validateFact(Fact<?> fact);
}
