package org.javaexpert.expert;

import java.util.HashSet;
import java.util.Set;

public record StringAttribute(String name, Set<String> values) implements Attribute {
    public StringAttribute(String name) {
        this(name, new HashSet<>());
    }
}
