package org.javaexpert.expert.predicate;

import org.javaexpert.expert.fact.Fact;
import org.javaexpert.expert.fact.StringFact;


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
    public boolean validateFact(Fact fact) {
        if (fact instanceof StringFact strFact) {
            return switch (operator) {
                case EQ -> strFact.value().contains(value());
                case NEQ -> !strFact.value().contains(value());
                default -> throw new IllegalStateException("Operator between string must be '=' or '<>'");
            };
        }

        return false;
    }
}
