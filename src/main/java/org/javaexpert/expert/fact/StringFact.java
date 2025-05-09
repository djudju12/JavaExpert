package org.javaexpert.expert.fact;

import java.util.Set;
import java.util.TreeSet;

public record StringFact(String name, Set<String> value) implements Fact {
    public StringFact(String name, String s) {
        this(name, new TreeSet<>());
        value().add(s);
    }
}