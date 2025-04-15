package org.javaexpert;

import java.util.Map;
import java.util.Set;

public sealed interface Predicate permits SimplePredicate, CompoundPredicate {

    PredicateType type();

    boolean isAbout(String attribute);

    boolean evaluate(Set<Rule> rules, Map<String, String> facts);

    enum PredicateType {
        SIMPLE, COMPOUND
    }
}
