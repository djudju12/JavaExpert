package org.javaexpert;


import org.javaexpert.expert.Expert;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
//        var expert = Expert.fromFile("example_numerico.ex");
//
//        expert.newFact("idade", 65);
//        expert.newFact("sexo", "masculino");

        var expert = Expert.fromFile("example.ex");
//        expert.newFact("controle_qualidade","rigoroso");
//        expert.newFact("acabamento","excelente");
//        expert.newFact("materia_prima","alta");
//        expert.newFact("processo_fabricacao","otimo");
//
        expert.newFact("controle_qualidade","fraco");
        expert.newFact("acabamento","defeituoso");
        expert.newFact("materia_prima","baixa");
        expert.newFact("processo_fabricacao","ruim");

        var conclusions = expert.think();
        System.out.println(expert.print());
    }
}