package org.javaexpert.expert;

import org.javaexpert.lexer.LogicOperator;

public record StringPredicate(
        String name,
        String value,
        LogicOperator operator
) implements Predicate {

    @Override
    public String toString() {
        return "%s: %s".formatted(name, value);
    }

    @Override
    public PredicateType type() {
        return PredicateType.SIMPLE;
    }

    @Override
    public boolean validateFact(Fact<?> fact) {
        return value().equals(fact.getValue());
    }
}
