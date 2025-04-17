package org.javaexpert.expert.predicate;

import org.javaexpert.TreeLogger;
import org.javaexpert.expert.Rule;
import org.javaexpert.expert.fact.Fact;

import java.util.Map;
import java.util.Set;

public sealed interface SimplePredicate extends Predicate permits StringPredicate, NumericPredicate {

    String name();

    boolean validateFact(Fact<?> fact, TreeLogger.Node parent);

    @Override
    default boolean isTrue(Set<Rule> rules, Map<String, Fact<?>> facts, TreeLogger.Node parent) {
        var fact = facts.get(name());
        if (fact != null) {
            return validateFact(fact, parent);
        }

        for (var rule: rules) {
            var isAboutPredicate = rule.conclusions().stream().anyMatch(f -> f.getName().equals(name()));
            if (isAboutPredicate && rule.isTrue(rules, facts, parent)) {
                return validateFact(facts.get(name()), parent);
            }
        }

        return false;
    }

}
