package kpi.skarlet.cad.synzer;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SynzerPanel extends JPanel {

    private JButton lexemesButton;
    private JButton idnsButton;
    private JButton consButton;
    private JButton lblsButton;
    private JTextArea table;
    private JScrollPane scrollPane;

    private JTextArea messageField;
    private JButton analyseText;

    private int hPadding = 10;
    private int vPadding = 10;
    private AbstractBorder border = MainWindow.createBorder(vPadding, hPadding, vPadding, hPadding);


    public SynzerPanel() {
        setLayout(new BorderLayout());
        init();
        this.setBorder(border);
    }

    private void init() {
        lexemesButton = new JButton("Lexemes");
        idnsButton = new JButton("Identifiers");
        consButton = new JButton("Constants");
        lblsButton = new JButton("Labels");

        table = new JTextArea();
        table.setTabSize(3);
        table.setEditable(false);
        scrollPane = new JScrollPane(table);

        messageField = new JTextArea(2, 0);

        analyseText = new JButton("Analyse");

        JPanel buttonsPanel = new JPanel();
        GridLayout gridLayoutButtons = new GridLayout(1, 4);
        gridLayoutButtons.setHgap(10);
        buttonsPanel.setLayout(gridLayoutButtons);
        buttonsPanel.add(lexemesButton);
        buttonsPanel.add(idnsButton);
        buttonsPanel.add(consButton);
        buttonsPanel.add(lblsButton);
        add(buttonsPanel, BorderLayout.NORTH);

        scrollPane.setBorder(MainWindow.createBorder(vPadding, 0, vPadding, 0));
        add(scrollPane, BorderLayout.CENTER);

//        GridLayout gridLayoutMsg = new GridLayout(1, 2);
//        c.fill = GridBagConstraints.HORIZONTAL;
        JPanel message_runPanel = new JPanel();

        GridBagConstraints c = new GridBagConstraints();
        message_runPanel.setLayout(new GridBagLayout());

        c.insets = new Insets(2, 2, 2, 2);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 1.0;
        c.weightx = 0.25;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        message_runPanel.add(new JScrollPane(messageField), c);
        c.weighty = 0.1;
        c.weightx = 0.0;
        c.gridwidth = 1;
        c.gridx = 3;
        message_runPanel.add(analyseText, c);

//        gridLayoutMsg.setHgap(10);
//        message_runPanel.setBorder(createBorder(10, 10, 10, 10));
        add(message_runPanel, BorderLayout.SOUTH);
    }

    private ActionListener createLexerProductsListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (MainWindow.getLexer().getExceptions().isEmpty())
                    MainWindow.getLexer().run();
//                if(lexer)
            }
        };
    }
}
