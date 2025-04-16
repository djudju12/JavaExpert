package org.javaexpert.expert;

public sealed interface Fact permits BooleanFact, StringFact {

    String getName();

}