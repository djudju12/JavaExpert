package org.javaexpert.expert.predicate;

import org.javaexpert.expert.fact.Fact;

public record NumericPredicate(
        String name,
        Integer value,
        LogicOperator operator
) implements SimplePredicate {

    @Override
    public String toString() {
        return "%s: %s".formatted(name, value);
    }

    @Override
    public boolean validateFact(Fact fact) {
        var factValue = (int) fact.value();
        return switch (operator()) {
            case EQ -> factValue == value();
            case NEQ -> factValue != value();
            case GT -> factValue > value();
            case GTE -> factValue >= value();
            case LT -> factValue < value();
            case LTE -> factValue <= value();
        };
    }
}