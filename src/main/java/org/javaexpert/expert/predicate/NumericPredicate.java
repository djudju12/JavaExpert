package org.javaexpert.expert.predicate;

import org.javaexpert.expert.TreeLogger;
import org.javaexpert.expert.fact.Fact;
import org.javaexpert.expert.fact.NumericFact;

public record NumericPredicate(
        String name,
        int value,
        LogicOperator operator
) implements SimplePredicate {

    @Override
    public String toString() {
        return "%s: %s".formatted(name, value);
    }

    @Override
    public boolean validateFact(Fact<?> fact, TreeLogger tree, TreeLogger.Node parent) {
        if (fact instanceof NumericFact numFact) {
            var ret = switch (operator()) {
                case EQ -> numFact.getValue() == value();
                case GT -> numFact.getValue() > value();
                case GTE -> numFact.getValue() >= value();
                case LT -> numFact.getValue() < value();
                case LTE -> numFact.getValue() <= value();
            };

            tree.appendf(
                    parent,
                    "'%s': %s %s %s? %s", name(), numFact.getValue(), operator(), value(),
                    ret ? "~>[VERDADEIRO]" : "~>[FALSO]"
            );

            return ret;
        } else {
            throw new RuntimeException("inconsistency between facts and rules");
        }
    }
}