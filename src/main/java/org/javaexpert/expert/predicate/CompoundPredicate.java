package org.javaexpert.expert.predicate;

import org.javaexpert.TreeLogger;
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
    public boolean isTrue(Set<Rule> rules, Map<String, Fact<?>> facts, TreeLogger tree, TreeLogger.Node parent) {
        var a = lhs().isTrue(rules, facts, tree, parent);
        tree.appendf(parent, "%s...", connector);
        return switch (connector) {
            case AND -> a && rhs().isTrue(rules, facts, tree, parent);
            case OR -> a || rhs().isTrue(rules, facts, tree, parent);
        };
    }

}
