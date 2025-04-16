package org.javaexpert.expert;

import java.util.HashSet;
import java.util.Set;

public record Attribute (String name, Set<String> values) {
    public Attribute(String name) {
        this(name, new HashSet<>());
    }
}
