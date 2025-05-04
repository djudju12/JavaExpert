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

        decisaoProblemaSaudeAutodelimitado_desmenorreia();
//        qualidadeProdutoExample();
//        presenteExample();
//        exemplAula();
    }

    private static void decisaoProblemaSaudeAutodelimitado_desmenorreia() throws IOException {
        var manager = new QuizManager("Problema de Saúde Autodelimitado: Dismenorreia", Expert.fromFile("dismenorreia.ex"));

        manager.setHomePage("Decisão Sobre Problema de Saúde Autodelimitado: Dismenorreia", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.");

        manager.addOptionsQuestion("Qual é a relação temporal entre o início da dor e o ciclo menstrual?", "dor_inicio");

        manager.addOptionsQuestion("Como se caracteriza a dor do paciente?", "caracteristica_dor");
        manager.addOptionsQuestion("Durante os ciclos menstruais, há alteração do local da dor?", "alteracao_do_local");

        manager.addOptionsQuestion("Como o paciente descreve a intensidade da dor?", "intesidade_dor");

        manager.addOptionsQuestion("Além da dor, há outros sintomas relevantes no quadro clínico?", "possui_outros_sintomas");
        manager.addMultiOptionsQuestion("O paciente apresenta algum dos seguintes sintomas associados?", "outros_sintomas", "Nenhum dos sintomas");

        manager.addOptionsQuestion("Qual foi a idade ou período em que surgiram as primeiras cólicas menstruais?", "primeira_dismenorreia");

        manager.addMultiOptionsQuestion("O paciente possui histórico de alguma das seguintes condições clínicas?", "historico_clinico", "Não possui");
        manager.addMultiOptionsQuestion("O paciente já tentou tratamento recente para os sintomas?", "historico_farmacoterapeutico", "Sem histórico de tratamentos relevante");

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

        public void addNumericQuestion(String text, String attr) {
            expert.addAskable(attr);
            quiz.newNumericQuestion(attr, text);
        }

        public void addOptionsQuestion(String text, String attr) {
            expert.addAskable(attr);
            quiz.newOptionQuestion(attr, text, expert.getAttributesValues(attr));
        }

        public void addMultiOptionsQuestion(String text, String attr) {
            expert.addAskable(attr);
            quiz.newMultiOptionQuestion(attr, text, expert.getAttributesValues(attr));
        }

        public void setHomePage(String title, String text){
            quiz.withHome(title, text);
        }

        public void addMultiOptionsQuestion(String text, String attr, String exclusiveOption) {
            expert.addAskable(attr);
            quiz.newMultiOptionQuestion(attr, text, expert.getAttributesValues(attr), exclusiveOption);
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