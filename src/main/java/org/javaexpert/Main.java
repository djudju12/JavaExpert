package org.javaexpert;


public class Main {

    public static void main(String[] args) {
        var expert = new Expert("Expert");
        expert.addAttribute("booleano_1");
        expert.addAttribute("booleano_2");
        expert.addAttribute("booleano_3");

        expert.withRule("R1")
                .iff("boleano_1").isTrue()
                .and("boleano_2").isTrue()
                .then("boleano_3").isFalse();

        expert.addObjectives("boleano_3");

        expert.addFact("boleano_1", true);
        expert.addFact("boleano_2", false);

        var conclusions = expert.think();
        if (conclusions.isEmpty()) {
            System.out.println("nothing has concluded");
        } else {
            System.out.println(conclusions);
        }
    }

    public static void main2(String[] args) {
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

        expert.addFact("controle_qualidade", "fraco");
        expert.addFact("acabamento", "defeituoso");
        expert.addFact("materia_prima", "baixa");
        expert.addFact("processo_fabricacao", "ruim");

        var conclusions = expert.think();
        if (conclusions.isEmpty()) {
            System.out.println("nothing has concluded");
        } else {
            System.out.println(conclusions);
        }
    }
}