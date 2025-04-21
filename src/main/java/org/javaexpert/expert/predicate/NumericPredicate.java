package org.javaexpert.expert.predicate;

import org.javaexpert.expert.fact.Fact;
import org.javaexpert.expert.fact.NumericFact;

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
    public boolean validateFact(Fact<?> fact) {
        if (fact instanceof NumericFact numFact) {
            return switch (operator()) {
                case EQ -> numFact.getValue().equals(value());
                case GT -> numFact.getValue() > value();
                case GTE -> numFact.getValue() >= value();
                case LT -> numFact.getValue() < value();
                case LTE -> numFact.getValue() <= value();
            };
        } else {
            throw new RuntimeException("inconsistency between facts and rules");
        }
    }
}