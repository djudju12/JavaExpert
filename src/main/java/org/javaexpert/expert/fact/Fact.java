package org.javaexpert.expert.fact;


public sealed interface Fact extends Comparable<Fact> permits NumericFact, StringFact {

    String name();
    Object value();

    @Override
    default int compareTo(Fact fact) {
        return fact.name().compareTo(name());
    }
}