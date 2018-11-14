package kpi.skarlet.cad.synzer;

import kpi.skarlet.cad.lexer.LexicalAnalyser;
import kpi.skarlet.cad.lexer.exceptions.LexicalException;
import kpi.skarlet.cad.lexer.lexemes.Lexeme;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SynzerPanel extends JPanel {

    private JButton lexemesButton;
    private JButton idnsButton;
    private JButton consButton;
    private JButton lblsButton;
    private JTable table;
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
        ActionListener lexerProductsListener = createLexerProductsListener();
        lexemesButton = new JButton("Lexemes");
        idnsButton = new JButton("Identifiers");
        consButton = new JButton("Constants");
        lblsButton = new JButton("Labels");
        lexemesButton.addActionListener(lexerProductsListener);
        idnsButton.addActionListener(lexerProductsListener);
        consButton.addActionListener(lexerProductsListener);
        lblsButton.addActionListener(lexerProductsListener);

//        table = new JTable();
        table = new JTable(new Object[][]{{"str1", "str2"}, {"2str1", "2str2"}}, new String[]{"col1", "col2"});
//        table.setTabSize(3);
//        table.setEditable(false);
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
//        message_runPanel.add(table, c);
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
                LexicalAnalyser lexer = MainWindow.getLexer();
                if (lexer.getLexemes().isEmpty())
                    lexer.run();
                if (lexer.getExceptions().isEmpty()) {
                    if (e.getSource() == lexemesButton) {
                        String[] columnNames = {"#",
                                "# рядка",
                                "Лексема",
                                "LEX code",
                                "IDN code",
                                "CON code",
                                "LBL code"};

                        Object[][] data = getLexemesData(lexer);

                        TableModel model = new DefaultTableModel(data, columnNames) {
                            @Override
                            public boolean isCellEditable(int row, int column) {
                                return false;
                            }
                        };
                        table.setModel(model);
                        table.getTableHeader().setUpdateTableInRealTime(false);

                        setColumnWidth();
                    }

                } else {
                    // show lexical exceptions
                    String[] columnNames = {"#", "# рядка", "Exception"};
                    Object[][] data = getExceptionsData(lexer);

                    TableModel model = new DefaultTableModel(data, columnNames) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                            return false;
                        }
                    };
                    table.setModel(model);
                    table.getTableHeader().setUpdateTableInRealTime(false);
                }
            }

            private void setColumnWidth() {
                table.getColumnModel().getColumn(0).setPreferredWidth(10);
                table.getColumnModel().getColumn(1).setPreferredWidth(35);
                table.getColumnModel().getColumn(4).setPreferredWidth(35);
                table.getColumnModel().getColumn(5).setPreferredWidth(35);
                table.getColumnModel().getColumn(6).setPreferredWidth(35);
            }

            private Object[][] getLexemesData(LexicalAnalyser lexer) {
                Stream<Lexeme> lexemeStream = lexer.getLexemes().stream();

                AtomicInteger i = new AtomicInteger(1);
                List<Object[]> lexemes = lexemeStream.map(l -> new Object[]{i.getAndIncrement(),
                        l.getLine(),
                        l.getLexemeName(),
                        l.getLexemeCode(),
                        (l.getLexemeCode() == LexicalAnalyser.IDN_CODE) ? l.getSpCode() : "",
                        (l.getLexemeCode() == LexicalAnalyser.CON_CODE) ? l.getSpCode() : "",
                        (l.getLexemeCode() == LexicalAnalyser.LBL_CODE) ? l.getSpCode() : ""}).collect(Collectors.toList());

                Object[][] data = new Object[lexemes.size()][];
                lexemes.toArray(data);
                return data;
            }

            private Object[][] getExceptionsData(LexicalAnalyser lexer) {
                Stream<LexicalException> exceptionStream = lexer.getExceptions().stream();
                AtomicInteger i = new AtomicInteger(1);
                List<Object[]> exceptions = exceptionStream.map(e -> new Object[]{i.getAndIncrement(),
                        e.getLine(),
                        e.getMessage()}).collect(Collectors.toList());

                Object[][] data = new Object[exceptions.size()][];
                exceptions.toArray(data);
                return data;
            }
        };
    }
}
