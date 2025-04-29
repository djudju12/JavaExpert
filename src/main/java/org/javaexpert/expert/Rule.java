package org.javaexpert.expert;

import org.javaexpert.expert.fact.Fact;
import org.javaexpert.expert.predicate.Predicate;

import java.util.Set;

public record Rule(String name, Predicate predicate, Set<Fact> conclusions) implements Comparable<Rule> {

    @Override
    public int compareTo(Rule o) {
        return o.name().compareTo(name());
    }
}
