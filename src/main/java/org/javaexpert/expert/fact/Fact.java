package org.javaexpert.expert.fact;

import java.util.Objects;

public abstract class Fact<T> implements Comparable<Fact<T>> {

    private final String name;
    private final T value;

    protected Fact(String name, T value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Fact{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }

    @Override
    public int compareTo(Fact<T> fact) {
        return fact.getName().compareTo(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fact<?> fact = (Fact<?>) o;
        return Objects.equals(name, fact.name) && Objects.equals(value, fact.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}