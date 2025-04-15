package org.javaexpert;

import java.util.Map;
import java.util.Set;

public sealed interface Predicate permits SimplePredicate, CompoundPredicate {

    PredicateType type();

    boolean evaluate(Set<Rule> rules, Map<String, Fact> facts);

    enum PredicateType {
        SIMPLE, COMPOUND
    }
}
