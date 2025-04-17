package org.javaexpert.expert.predicate;

import org.javaexpert.TreeLogger;
import org.javaexpert.expert.Rule;
import org.javaexpert.expert.fact.Fact;

import java.util.Map;
import java.util.Set;

import static org.javaexpert.expert.predicate.LogicConnector.AND;

public record CompoundPredicate(
        Predicate lhs,
        Predicate rhs,
        LogicConnector connector
) implements Predicate {

    private static final TreeLogger logger = TreeLogger.instance();

    @Override
    public boolean isTrue(Set<Rule> rules, Map<String, Fact<?>> facts, TreeLogger.Node parent) {
        var a = lhs().isTrue(rules, facts, parent);
        logger.appendf(parent, "%s...", connector);
        return switch (connector) {
            case AND -> a && rhs().isTrue(rules, facts, parent);
            case OR -> a || rhs().isTrue(rules, facts, parent);
        };
    }

}
