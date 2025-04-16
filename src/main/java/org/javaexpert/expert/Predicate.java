package org.javaexpert.expert;

import java.util.Map;
import java.util.Set;

public sealed interface Predicate permits SimplePredicate, CompoundPredicate {

    PredicateType type();

    boolean isTrue(Set<Rule> rules, Map<String, Fact> facts);

    enum PredicateType {
        SIMPLE, COMPOUND
    }
}
