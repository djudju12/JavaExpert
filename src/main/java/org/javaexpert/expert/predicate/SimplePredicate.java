package org.javaexpert.expert.predicate;

import org.javaexpert.expert.fact.Fact;

public sealed interface SimplePredicate extends Predicate permits StringPredicate, NumericPredicate {

    String name();
    Object value();
    LogicOperator operator();

    boolean validateFact(Fact<?> fact);

}
