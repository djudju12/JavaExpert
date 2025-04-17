package org.javaexpert.expert.predicate;

import org.javaexpert.TreeLogger;
import org.javaexpert.expert.Rule;
import org.javaexpert.expert.fact.Fact;

import java.util.Map;
import java.util.Set;

import static org.javaexpert.expert.predicate.LogicConnector.AND;
import static org.javaexpert.expert.predicate.LogicConnector.OR;

public record CompoundPredicate(
        Predicate lhs,
        Predicate rhs,
        LogicConnector connector
) implements Predicate {

    @Override
    public boolean isTrue(Set<Rule> rules, Map<String, Fact<?>> facts, TreeLogger tree, TreeLogger.Node parent) {
        var a = lhs().isTrue(rules, facts, tree, parent);
        return switch (connector) {
            case AND -> (a && tree.appendf(parent, "%s...", connector) != null) && rhs().isTrue(rules, facts, tree, parent);
            case OR -> (a && tree.appendf(parent, "%s...", connector) != null) || rhs().isTrue(rules, facts, tree, parent);
        };
    }

}
