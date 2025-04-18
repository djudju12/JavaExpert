package org.javaexpert.ui;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.text.NumberFormat;
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


        cards.add(newQuestion(
                "idade?",
                (id, answer) -> System.out.printf("%d -> %d\n", id, answer)
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

    private Question<Integer> newQuestion(String text, OnQuestionAnswered<Integer> onQuestionAnswered) {
        return new NumericQuestion(counter++, text, onQuestionAnswered);
    }

    private Question<String> newQuestion(String text, Set<String> options, OnQuestionAnswered<String> onQuestionAnswered) {
        return new OptionQuestion(counter++, text, options, onQuestionAnswered);
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
        if ("Anterior".equals(cmd)) previousCard();
        else nextCard();
        updateButtons();
    }

    @FunctionalInterface
    public interface OnQuestionAnswered<T> { void apply(int id, T answer); }

    static class NumericQuestion extends Question<Integer> {

        private final JFormattedTextField field;

        public NumericQuestion(int id, String text, OnQuestionAnswered<Integer> onQuestionAnswered) {
            super(id, text, onQuestionAnswered);
            var numPanel = new JPanel();
            numPanel.setLayout(new BoxLayout(numPanel, BoxLayout.Y_AXIS));

            var format = NumberFormat.getIntegerInstance();
            format.setGroupingUsed(false);

            var formatter = new NumberFormatter(format);
            formatter.setValueClass(Integer.class);
            formatter.setAllowsInvalid(false);
            formatter.setMinimum(0);
            formatter.setMaximum(Integer.MAX_VALUE);

            field = new JFormattedTextField(formatter);
            field.setColumns(10);
            field.setMaximumSize(field.getPreferredSize());
            field.addPropertyChangeListener("value", this::onValueChanged);
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.BLACK),
                    BorderFactory.createEmptyBorder(2, 4, 2, 4)
            ));

            numPanel.add(field);
            add(numPanel, BorderLayout.LINE_START);
        }

        public void onValueChanged(PropertyChangeEvent evt) {
            var fieldValue = (Number) field.getValue();
            if (fieldValue != null) {
                onQuestionAnswered.apply(id, fieldValue.intValue());
            }
        }
    }

    static class OptionQuestion extends Question<String> implements ItemListener {

        private final List<JCheckBox> checkBoxes = new ArrayList<>();

        OptionQuestion(int id, String text, Set<String> options, OnQuestionAnswered<String> onQuestionAnswered) {
            super(id, text, onQuestionAnswered);

            var checkBoxPanel = new JPanel();
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

    static abstract class Question<T> extends JPanel {

        protected final int id;
        protected final OnQuestionAnswered<T> onQuestionAnswered;

        Question(int id, String text, OnQuestionAnswered<T> onQuestionAnswered) {
            super(new BorderLayout(20, 20));
            this.id = id;
            this.onQuestionAnswered = onQuestionAnswered;
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            var questionLabel = new JLabel(text);
            questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
            add(questionLabel, BorderLayout.NORTH);
        }
    }

    public static void main(String[] args) {
        var frame = new JFrame("Quiz Example");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Center the window
        frame.setResizable(false);
        new Quiz().addComponentToPane(frame.getContentPane());
        frame.pack();
        frame.setVisible(true);
    }
}
