package org.javaexpert.expert.attribute;

import java.util.Set;
import java.util.TreeSet;

public record StringAttribute(String name, Set<String> values) implements Attribute {
    public StringAttribute(String name) {
        this(name, new TreeSet<>());
    }
}
