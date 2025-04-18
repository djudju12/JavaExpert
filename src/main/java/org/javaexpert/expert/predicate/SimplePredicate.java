package org.javaexpert.expert.predicate;

import org.javaexpert.expert.TreeLogger;
import org.javaexpert.expert.Rule;
import org.javaexpert.expert.fact.Fact;

import java.util.Map;
import java.util.Set;

public sealed interface SimplePredicate extends Predicate permits StringPredicate, NumericPredicate {

    String name();

    boolean validateFact(Fact<?> fact, TreeLogger tree, TreeLogger.Node parent);

    @Override
    default boolean isTrue(Set<Rule> rules, Map<String, Fact<?>> facts, TreeLogger tree, TreeLogger.Node parent) {
        var fact = facts.get(name());
        if (fact != null) {
            return validateFact(fact, tree, parent);
        }

        var child = tree.appendf(parent, "PROCURANDO '%s'...", name());

        for (var rule: rules) {
            var isAboutPredicate = rule.conclusions().stream().anyMatch(f -> f.getName().equals(name()));
            if (isAboutPredicate && rule.isTrue(rules, facts, tree, child)) {
                return validateFact(facts.get(name()), tree, parent);
            }
        }

        return false;
    }

}
