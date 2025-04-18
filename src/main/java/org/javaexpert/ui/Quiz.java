package org.javaexpert.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Quiz implements ActionListener {

    private final JPanel cards = new JPanel(new CardLayout());

    private final JButton backButton = new JButton("Anterior");
    private final JButton nextButton = new JButton("Próximo");
    private int current = 0;
    private int counter = 0;

    public Quiz() { }

    public void addComponentToPane(Container pane) {
        cards.add(newQuestion(
                "What are your favorite programming languages?",
                Set.of("Java", "Python", "JavaScript", "C++"),
                (id, answer) -> System.out.printf("%d -> %s\n", id, answer)));
        cards.add(newQuestion(
                "Hello",
                Set.of("World", "Saylor"),
                (id, answer) -> System.out.printf("%d -> %s\n", id, answer)
        ));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        buttonPanel.add(backButton);
        buttonPanel.add(nextButton);

        backButton.addActionListener(this);
        nextButton.addActionListener(this);

        pane.add(cards);
        pane.add(buttonPanel, BorderLayout.SOUTH);
        updateButtons();
    }

    private Question newQuestion(String text, Set<String> options, OnQuestionAnswered onQuestionAnswered) {
        return new Question(counter++, text, options, onQuestionAnswered);
    }

    private void nextCard() {
        if (notInLastCard()) {
            current++;
            ((CardLayout)cards.getLayout()).next(cards);
        }
    }

    private void previousCard() {
        if (notInFirstCard()) {
            current--;
            ((CardLayout)cards.getLayout()).previous(cards);
        }
    }

    private boolean notInLastCard() {
        return current != (cards.getComponentCount() - 1);
    }

    private boolean notInFirstCard() {
        return current != 0;
    }

    private void updateButtons() {
        backButton.setEnabled(notInFirstCard());
        nextButton.setEnabled(notInLastCard());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        var cmd = e.getActionCommand();
        if ("Anterior".equals(cmd)) {
            previousCard();
        } else {
            nextCard();
        }

        updateButtons();
    }

    @FunctionalInterface
    public interface OnQuestionAnswered {

        void apply(int questionId, String answer);

    }

    public static class Question extends JPanel implements ItemListener {

        private final int id;
        private final OnQuestionAnswered onQuestionAnswered;
        private final List<JCheckBox> checkBoxes = new ArrayList<>();

        public Question(int id, String text, Set<String> options,  OnQuestionAnswered onQuestionAnswered) {
            super(new BorderLayout(20, 20));
            this.id = id;
            this.onQuestionAnswered = onQuestionAnswered;
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JLabel questionLabel = new JLabel(text);
            questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
            add(questionLabel, BorderLayout.NORTH);

            JPanel checkBoxPanel = new JPanel();
            checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));

            options.forEach(opt -> {
                var cb = new JCheckBox(opt);
                checkBoxes.add(cb);
                checkBoxPanel.add(cb);
                cb.addItemListener(this);
            });

            add(checkBoxPanel, BorderLayout.CENTER);
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                this.onQuestionAnswered.apply(this.id, null);
                return;
            }

            for (var cb: checkBoxes) {
                var selectedCb = e.getItemSelectable();
                if (cb != selectedCb && cb.isSelected()) {
                    cb.setSelected(false);
                }
            }

            this.onQuestionAnswered.apply(this.id, ((JCheckBox) e.getItem()).getText());
        }
    }

    public static void main(String[] args) {
        var frame = new JFrame("Quiz Example");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Center the window
        frame.setResizable(false);
        var quiz = new Quiz();
        quiz.addComponentToPane(frame.getContentPane());
        frame.pack();
        frame.setVisible(true);
    }
}
