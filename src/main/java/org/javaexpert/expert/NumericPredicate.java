package org.javaexpert.expert;

import org.javaexpert.lexer.LogicOperator;

import java.util.Map;
import java.util.Set;

record NumericPredicate(
    String name,
    float value,
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
        if (fact instanceof NumericFact numFact) {
            return switch (operator()) {
                case EQ -> numFact.getValue() == value();
                case GT -> numFact.getValue() > value();
                case GTE -> numFact.getValue() >= value();
                case LT -> numFact.getValue() < value();
                case LTE -> numFact.getValue() <= value();
            };
        } else {
            throw new RuntimeException("some inconsistency between facts and rules");
        }
    }
}