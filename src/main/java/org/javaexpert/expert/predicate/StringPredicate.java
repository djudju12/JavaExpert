package org.javaexpert.expert.predicate;

import org.javaexpert.expert.fact.Fact;

public record StringPredicate(
        String name,
        String value,
        LogicOperator operator
) implements SimplePredicate {

    @Override
    public String toString() {
        return "%s: %s".formatted(name, value);
    }

    @Override
    public boolean validateFact(Fact<?> fact) {
        return switch (operator) {
            case EQ -> value().equals(fact.getValue());
            case NEQ -> !value().equals(fact.getValue());
            default -> throw new IllegalStateException("Operator between string must be '=' or '<>'");
        };
    }
}
