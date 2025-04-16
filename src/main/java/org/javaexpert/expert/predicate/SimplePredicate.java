package org.javaexpert.expert.predicate;

import org.javaexpert.expert.Rule;
import org.javaexpert.expert.fact.Fact;

import java.util.Map;
import java.util.Set;

public sealed interface SimplePredicate extends Predicate permits StringPredicate, NumericPredicate {

    String name();

    boolean validateFact(Fact<?> fact);

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

}
