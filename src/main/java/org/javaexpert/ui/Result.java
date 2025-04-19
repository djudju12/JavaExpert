package org.javaexpert.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import static org.javaexpert.Main.setUIFont;

public class Result {

    public Result() { }

    private void addComponentToPane(Container pane) {
        var tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        var hist = makeTextPanel(
                        """
                        > ENTRANDO NA REGRA 'R6':
                        |--> PROCURANDO 'durabilidade'...
                        |  |--> ENTRANDO NA REGRA 'R2':
                        |  | |--> 'materia_prima': 'baixa' = 'alta'?    [FALSO]
                        |  | `--> REGRA 'R2' NÃO APLICADA
                        |  `--> ENTRANDO NA REGRA 'R1':
                        |    |--> 'materia_prima': 'baixa' = 'baixa'?   [VERDADEIRO]
                        |    `--> REGRA 'R1' APLICADA
                        |      `--> 'durabilidade' := 'baixa'
                        |--> 'durabilidade': 'baixa' = 'alta'?          [FALSO]
                        `--> REGRA 'R6' NÃO APLICADA
                        
                        > ENTRANDO NA REGRA 'R5':
                        |--> 'durabilidade': 'baixa' = 'baixa'?         [VERDADEIRO]
                        `--> REGRA 'R5' APLICADA
                          `--> 'qualidade_final' := 'rejeitavel'

                        >>>>> REGRA ACEITA: 'R5' <<<<<
                        """);

        tabbedPane.addTab("Histórico", hist);

        var menuBar = new JMenuBar();
        var menu = new JMenu("Nova");
        menuBar.add(menu);
        menuBar.setBorder(new MatteBorder(0, 0, 10, 0, menuBar.getBackground()));

        pane.add(menuBar, BorderLayout.NORTH);
        pane.add(tabbedPane, BorderLayout.CENTER);
    }

    protected JComponent makeTextPanel(String text) {
        var panel = new JPanel();
        panel.setLayout(new BorderLayout());

        var textArea = new JTextArea(text);
        textArea.setSize(300, 300);
        textArea.setEditable(false);
        textArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        var scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private static void createAndShowGUI() {
        var frame = new JFrame("Resumo");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);

        var component = new Result();
        component.addComponentToPane(frame.getContentPane());

        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        setUIFont(new javax.swing.plaf.FontUIResource("Monospaced", Font.PLAIN, 14));

        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(() -> {
            //Turn off metal's use of bold fonts
            UIManager.put("swing.boldMetal", Boolean.FALSE);
            createAndShowGUI();
        });
    }
}
