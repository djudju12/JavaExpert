package org.javaexpert.expert.attribute;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class StringAttribute implements Attribute {
    private final String name;
    private final Set<String> values;
    private final List<String> orderedValues;

    public StringAttribute(String name) {
        this.name = name;
        values = new HashSet<>();
        orderedValues = new ArrayList<>();
    }

    @Override
    public String name() {
        return name;
    }

    public void addValue(String value) {
        values.add(value);
        orderedValues.add(value);
    }

    public List<String> getOrderedValues() {
        return orderedValues;
    }

    public boolean contains(String value) {
        return values.contains(value);
    }

}
