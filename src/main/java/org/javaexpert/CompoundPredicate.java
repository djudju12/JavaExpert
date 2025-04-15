package org.javaexpert;

import java.util.Map;
import java.util.Set;

public record CompoundPredicate(
        Predicate lhs,
        Predicate rhs,
        LogicConnector connector
) implements Predicate {

    @Override
    public PredicateType type() {
        return PredicateType.COMPOUND;
    }

    @Override
    public boolean evaluate(Set<Rule> rules, Map<String, Fact> facts) {
        return switch (connector) {
            case AND -> lhs().evaluate(rules, facts) && rhs().evaluate(rules, facts);
            case OR -> lhs().evaluate(rules, facts) || rhs().evaluate(rules, facts);
        };
    }

}
