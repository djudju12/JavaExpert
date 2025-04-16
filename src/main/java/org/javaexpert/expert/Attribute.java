package org.javaexpert.expert;

public sealed interface Attribute permits NumericAttribute, StringAttribute {
    String name();
}
