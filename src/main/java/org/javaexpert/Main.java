package org.javaexpert;


import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        var expert = new Expert("Expert");

        expert.addAttributes("materia_prima"       , "alta"      , "media"        , "baixa"     );
        expert.addAttributes("processo_fabricacao" , "otimo"     , "aceitavel"    , "ruim"      );
        expert.addAttributes("controle_qualidade"  , "rigoroso"  , "moderado"     , "fraco"     );
        expert.addAttributes("durabilidade"        , "alta"      , "media"        , "baixa"     );
        expert.addAttributes("acabamento"          , "excelente" , "regular"      , "defeituoso");
        expert.addAttributes("satisfacao_cliente"  , "alta"      , "media"        , "baixa"     );
        expert.addAttributes("qualidade_final"     , "aceitavel" , "rejeitavel"                 );

        expert.withRule("R1")
              .iff("materia_prima").is("baixa")
              .or("processo_fabricacao").is("ruim")
              .then("durabilidade").is("baixa");

        expert.withRule("R2")
              .iff("materia_prima").is("alta")
              .and("processo_fabricacao").is("otimo")
              .then("durabilidade").is("alta");

        expert.withRule("R3")
              .iff("controle_qualidade").is("rigoroso")
              .and("acabamento").is("excelente")
              .then("satisfacao_cliente").is("alta");

        expert.withRule("R4")
              .iff("controle_qualidade").is("fraco")
              .or("acabamento").is("defeituoso")
              .then("satisfacao_cliente").is("baixa");

        expert.withRule("R5")
              .iff("durabilidade").is("baixa")
              .or("satisfacao_cliente").is("baixa")
              .then("qualidade_final").is("rejeitavel");

        expert.withRule("R6")
              .iff("durabilidade").is("alta")
              .and("satisfacao_cliente").is("alta")
              .then("qualidade_final").is("aceitavel");

        expert.addObjectives("qualidade_final");

        var facts = new HashMap<String, String>();
        facts.put("controle_qualidade", "fraco");
        facts.put("acabamento", "defeituoso");
        facts.put("materia_prima", "baixa");
        facts.put("processo_fabricacao", "ruim");

        expert.think(facts)
                .ifPresentOrElse(System.out::println, () -> System.out.println("Cannot conclude"));

        System.out.println(facts);
    }
}