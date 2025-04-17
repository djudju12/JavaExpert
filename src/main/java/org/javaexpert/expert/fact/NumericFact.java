package org.javaexpert.expert.fact;

public class NumericFact extends Fact<Float> {
    public NumericFact(String name, int value) {
        super(name, (float) value);
    }
}