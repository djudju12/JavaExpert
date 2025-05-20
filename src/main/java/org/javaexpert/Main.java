package org.javaexpert;


import org.javaexpert.expert.Expert;
import org.javaexpert.ui.Quiz;
import org.javaexpert.ui.Result;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.IOException;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws IOException {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        setUIFont(new javax.swing.plaf.FontUIResource("SansSerif", Font.PLAIN, 16));

        decisaoProblemaSaudeAutodelimitado_dismenorreia();
//        qualidadeProdutoExample();
//        presenteExample();
//        exemplAula();
    }

    private static void decisaoProblemaSaudeAutodelimitado_dismenorreia() throws IOException {
        var manager = new QuizManager("Problema de Saúde Autolimitado: Dismenorreia", Expert.fromFile("dismenorreia.ex"));

        manager.setHomePage("Decisão Sobre Problema de Saúde Autolimitado: Dismenorreia",
            """
            Este sistema foi desenvolvido com o objetivo de auxiliar profissionais farmacêuticos na condução do atendimento clínico relacionado à dismenorreia — uma condição comum caracterizada por dor uterina durante o período menstrual. Embora muitas vezes seja autolimitada, éessencial avaliar cuidadosamente cada caso, a fim de identificar possíveis sinais de outras condições, como endometriose, adenomiose ou miomas uterinos.
            
            Com base no algoritmo clínico oficial do Conselho Federal de Farmácia (CFF), o sistema orienta o processo de anamnese por meio de perguntas estruturadas, permitindo ao farmacêutico avaliar os sintomas relatados e, assim, tomar decisões mais seguras e embasadas sobre o plano de cuidado. Caso seja identificado que pode se tratar de um quadro mais grave, o sistema indicará a necessidade de encaminhamento para outro profissional ou serviço de saúde.
            
            De forma simples, prática e segura, esta ferramenta busca otimizar a atuação clínica do farmacêutico, garantindo um atendimento mais preciso, eficiente e centrado no paciente.
            """
        );

        manager.addOptionsQuestion("Qual é a relação temporal entre o início da dor e o ciclo menstrual?", "dor_inicio")
                .withWhy("A relação da dor com o ciclo menstrual pode indicar origem ginecológica — dor cíclica, como a que antecede a menstruação, é típica da dismenorreia.");

        manager.addOptionsQuestion("Como se caracteriza a dor do paciente?", "caracteristica_dor")
                .withWhy("A dor suprapúbica é localizada abaixo do umbigo e pode ter relação com o ciclo menstrual, sendo importante para diferenciar causas ginecológicas de outras condições abdominais.");

        manager.addOptionsQuestion("Durante os ciclos menstruais, há alteração do local da dor?", "alteracao_do_local")
                .withWhy("A mudança no local da dor durante o ciclo menstrual pode sugerir envolvimento de diferentes estruturas pélvicas, sendo um sinal relevante para investigação de condições como endometriose.");

        manager.addOptionsQuestion("Como o paciente descreve a intensidade da dor?", "intesidade_dor")
                .withWhy("A intensidade da dor e sua resposta a medicamentos ajudam a diferenciar quadros funcionais de condições mais graves que exigem investigação aprofundada.");

        manager.addOptionsQuestion("Além da dor, há outros sintomas relevantes no quadro clínico?", "possui_outros_sintomas")
                .withWhy("Sintomas associados ajudam a identificar a causa da dor, pois certos padrões — como dor com diarreia ou alterações urinárias — sugerem condições específicas, como infecções ou distúrbios intestinais.");

        manager
                .addMultiOptionsQuestion("O paciente apresenta algum dos seguintes sintomas associados?", "outros_sintomas")
                .withExclusiveOption("Nenhum dos sintomas")
                .withWhy("Sintomas associados ajudam a identificar a causa da dor, pois certos padrões sugerem condições específicas.");

        manager.addOptionsQuestion("Qual foi o período em que surgiram as primeiras cólicas menstruais?", "primeira_dismenorreia")
                .withWhy("A idade de início das cólicas menstruais pode ajudar a distinguir entre dismenorreia primária (geralmente precoce) e causas secundárias, como endometriose, que costumam surgir após os 25 anos.");

        manager
                .addMultiOptionsQuestion("O paciente possui histórico de alguma das seguintes condições clínicas?", "historico_clinico")
                .withExclusiveOption("Não possui")
                .withWhy("Condições clínicas pré-existentes podem influenciar tanto a causa da dor quanto a escolha do tratamento, exigindo maior cautela em abordagens medicamentosas.");

        manager
                .addMultiOptionsQuestion("O paciente já tentou tratamento recente para os sintomas?", "historico_farmacoterapeutico")
                .withExclusiveOption("Sem histórico de tratamentos relevante")
                .withWhy("Histórico de tratamentos anteriores, especialmente falhas ou reações adversas, pode indicar a necessidade de mudar a abordagem terapêutica ou investigar outras causas para os sintomas.");

        manager.runQuiz();
    }

    private static void exemplAula() throws IOException {
        var manager = new QuizManager("Exemplo: Aula", Expert.fromFile("examplo_aula.ex"));
        manager.addOptionsQuestion("Você tem episódios de lapsos de memória?", "Lapsos");
        manager.addOptionsQuestion("Quando você descreve suas lembranças, qual o nível de detalhamento?", "Lembranças");
        manager.addOptionsQuestion("Como estão os movimentos do seu diafragma?", "Movimentos");
        manager.addOptionsQuestion("Como está a sua respiração?", "Respiração");
        manager.addOptionsQuestion("Como está a sua tosse?", "Tosse");
        manager.runQuiz();
    }

    private static void presenteExample() throws IOException {
        var manager = new QuizManager("Exemplo: Presente", Expert.fromFile("example_numerico.ex"));
        manager.addNumericQuestion("Qual a idade do aniversariante?", "idade");
        manager.addOptionsQuestion("Qual é o sexo do aniversáriante?", "sexo");
        manager.runQuiz();
    }

    private static void qualidadeProdutoExample() throws IOException {
        var manager = new QuizManager("Exemplo: Qualidade de Produto", Expert.fromFile("example.ex"));
        manager.addOptionsQuestion("Como foi o controle de qualidade?", "controle_qualidade");
        manager.addOptionsQuestion("Como está o acabamento do produto?", "acabamento");
        manager.addOptionsQuestion("Qual a qualidade da materia prima?", "materia_prima");
        manager.addOptionsQuestion("Qual o nível do processo de fabricação?", "processo_fabricacao");

        manager.runQuiz();
    }

    public static void setUIFont(FontUIResource f){
        var keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            var key = keys.nextElement();
            var value = UIManager.get (key);
            if (value instanceof FontUIResource)
                UIManager.put(key, f);
        }
    }

    private static class QuizManager {
        private final Expert expert;
        private final Quiz quiz;

        private QuizManager(String title, Expert expert) {
            this.expert = expert;
            this.quiz = new Quiz(title);

            quiz.onQuestionAsnwered((id, answer) -> {
                if (answer == null) return id;
                if (answer instanceof Set<?> answers) {
                    expert.removeFact(id);
                    answers.forEach(a -> expert.newFact(id, a));
                } else {
                    expert.newFact(id, answer);
                }
                var questionOpt = expert.thinkIfNotConclusiveAskQuestion();
                if (questionOpt.isEmpty()) {
                    runResult();
                    return null;
                }

                return questionOpt.get();
            });

        }

        public Quiz.NumericQuestion addNumericQuestion(String text, String attr) {
            expert.addAskable(attr);
            return quiz.newNumericQuestion(attr, text);
        }

        public Quiz.OptionQuestion addOptionsQuestion(String text, String attr) {
            expert.addAskable(attr);
            return quiz.newOptionQuestion(attr, text, expert.getAttributesValues(attr));
        }

        public Quiz.MultiOptionQuestion addMultiOptionsQuestion(String text, String attr) {
            expert.addAskable(attr);
            return quiz.newMultiOptionQuestion(attr, text, expert.getAttributesValues(attr));
        }

        public void setHomePage(String title, String text){
            quiz.withHome(title, text);
        }

        private void runResult() {
            var result = new Result(expert);
            result.onNew(this::runQuiz);
            result.createAndShowGUI();
        }

        public void runQuiz() {
            expert.clearMemory();
            var firstQuestionOpt = expert.thinkIfNotConclusiveAskQuestion();
            if (firstQuestionOpt.isPresent()) {
                quiz.setFirstQuestion(firstQuestionOpt.get());
                quiz.runGui(true);
            } else {
                runResult();
            }
        }
    }
}