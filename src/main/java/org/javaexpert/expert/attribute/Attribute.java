package org.javaexpert.expert.attribute;

public sealed interface Attribute permits NumericAttribute, StringAttribute {
    String name();
}
