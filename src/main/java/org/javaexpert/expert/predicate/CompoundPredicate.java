package org.javaexpert.expert.predicate;

public record CompoundPredicate(
        Predicate lhs,
        Predicate rhs,
        LogicConnector connector
) implements Predicate { }
