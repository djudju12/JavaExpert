package org.javaexpert;


import org.javaexpert.expert.Expert;
import org.javaexpert.expert.fact.Fact;
import org.javaexpert.expert.fact.NumericFact;
import org.javaexpert.expert.fact.StringFact;

import java.io.IOException;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) throws IOException {
        var expert = Expert.fromFile("example_numerico.ex");

        expert.newFact("idade", "65");
        expert.newFact("sexo", "masculino");

//        facts.put("controle_qualidade", new StringFact("controle_qualidade", "rigoroso"));
//        facts.put("acabamento", new StringFact("acabamento", "excelente"));
//        facts.put("materia_prima", new StringFact("materia_prima", "alta"));
//        facts.put("processo_fabricacao", new StringFact("processo_fabricacao", "otimo"));

//        facts.put("controle_qualidade", new StringFact("controle_qualidade", "fraco"));
//        facts.put("acabamento", new StringFact("acabamento", "defeituoso"));
//        facts.put("materia_prima", new StringFact("materia_prima", "baixa"));
//        facts.put("processo_fabricacao", new StringFact("processo_fabricacao", "ruim"));

        var conclusions = expert.think();
        System.out.println(conclusions);
    }
}