package org.javaexpert.expert.fact;

import java.util.HashSet;
import java.util.Set;

public record StringFact(String name, Set<String> value) implements Fact {
    public StringFact(String name, String s) {
        this(name, new HashSet<>());
        value().add(s);
    }
}