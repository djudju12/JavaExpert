package org.javaexpert.expert.predicate;

import org.javaexpert.expert.Rule;
import org.javaexpert.expert.fact.Fact;

import java.util.Map;
import java.util.Set;

public record CompoundPredicate(
        Predicate lhs,
        Predicate rhs,
        LogicConnector connector
) implements Predicate {

    @Override
    public boolean isTrue(Set<Rule> rules, Map<String, Fact<?>> facts) {
        return switch (connector) {
            case AND -> lhs().isTrue(rules, facts) && rhs().isTrue(rules, facts);
            case OR -> lhs().isTrue(rules, facts) || rhs().isTrue(rules, facts);
        };
    }

}
