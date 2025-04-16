package org.javaexpert;


import org.javaexpert.expert.Expert;
import org.javaexpert.expert.Fact;
import org.javaexpert.expert.StringFact;

import java.io.IOException;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) throws IOException {
        var expert = Expert.fromFile("example.ex");
        var facts = new HashMap<String, Fact>();

        facts.put("controle_qualidade", new StringFact("controle_qualidade", "rigoroso"));
        facts.put("acabamento", new StringFact("acabamento", "excelente"));
        facts.put("materia_prima", new StringFact("materia_prima", "alta"));
        facts.put("processo_fabricacao", new StringFact("processo_fabricacao", "otimo"));

//        facts.put("controle_qualidade", new StringFact("controle_qualidade", "fraco"));
//        facts.put("acabamento", new StringFact("acabamento", "defeituoso"));
//        facts.put("materia_prima", new StringFact("materia_prima", "baixa"));
//        facts.put("processo_fabricacao", new StringFact("processo_fabricacao", "ruim"));

        var conclusions = expert.thinkAboutFacts(facts);
        System.out.println(conclusions);
    }
}