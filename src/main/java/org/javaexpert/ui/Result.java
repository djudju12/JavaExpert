package org.javaexpert.ui;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.util.Map;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Result {

    private final Quiz quiz;
    private final JFrame frame;
    private final String hist;
    private final Map<String, Object> allFacts; // attrName -> attrValue
    private final Map<String, Object> objectives; // attrName -> attrValue

    private Runnable onNewFn;

    public Result(Quiz quiz, Map<String, Object> allFacts, Map<String, Object> objectives, String hist) {
        this.quiz = quiz;
        this.allFacts = allFacts;
        this.objectives = objectives;
        this.hist = hist;
        this.frame = new JFrame("Resumo");
    }

    public void onNew(Runnable fn) {
        this.onNewFn = fn;
    }

    private void addComponentToPane(Container pane) {
        var tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.addTab("Conclusões", makeMapPanel(parseMap(objectives)));
        tabbedPane.addTab("Resultados", makeMapPanel(parseMap(allFacts)));
        tabbedPane.addTab("Histórico", makeTextPanel(hist));

        pane.add(makeMenuBar(), BorderLayout.NORTH);
        pane.add(tabbedPane, BorderLayout.CENTER);
    }

    private JMenuBar makeMenuBar() {
        var menuBar = new JMenuBar();
        var menu = new JMenu("Nova");
        menu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                onNewFn.run();
                quiz.runGui();
                frame.dispose();
            }

            @Override
            public void menuDeselected(MenuEvent e) { }

            @Override
            public void menuCanceled(MenuEvent e) { }
        });

        menuBar.add(menu);
        menuBar.setBorder(new MatteBorder(0, 0, 10, 0, menuBar.getBackground()));
        return menuBar;
    }

    protected JComponent makeMapPanel(Object[][] data) {
        var panel = new JPanel();
        panel.setLayout(new BorderLayout());

        var columns = new String[] { "Atributo", "Valor" };
        var table = new JTable(data, columns);
        var header = table.getTableHeader();

        header.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        table.setFillsViewportHeight(true);
        table.setBorder(new MatteBorder(0, 1, 1, 1, Color.GRAY));

        var scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private static Object[][] parseMap(Map<String, Object> map) {
        var data = new Object[map.size()][2];
        int i = 0;
        for (var key: map.keySet()) {
            data[i][0] = key;
            data[i][1] = map.get(key);
            i += 1;
        }
        return data;
    }

    private JComponent makeTextPanel(String text) {
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

    public void close() {
        frame.dispose();
    }

    public void createAndShowGUI() {
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);

        addComponentToPane(frame.getContentPane());

        frame.setSize(500, 500);
        frame.setVisible(true);
    }

}
