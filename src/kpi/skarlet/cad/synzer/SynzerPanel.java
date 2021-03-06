package kpi.skarlet.cad.synzer;

import kpi.skarlet.cad.lexer.LexicalAnalyser;
import kpi.skarlet.cad.lexer.exceptions.LexicalException;
import kpi.skarlet.cad.lexer.lexemes.Constant;
import kpi.skarlet.cad.lexer.lexemes.Identifier;
import kpi.skarlet.cad.lexer.lexemes.Label;
import kpi.skarlet.cad.lexer.lexemes.Lexeme;
import kpi.skarlet.cad.synzer.transition_table.DataTableField;
import kpi.skarlet.cad.synzer.transition_table.MultiLineTableCellRenderer;
import kpi.skarlet.cad.synzer.transition_table.State;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
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
    private JButton outputTable;
    private JButton inputTable;

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

        table = new JTable();
        scrollPane = new JScrollPane(table);

        messageField = new JTextArea(2, 0);
        messageField.setEditable(false);

        analyseText = new JButton("Analyse");
        analyseText.addActionListener(lexerProductsListener);

        outputTable = new JButton("Get Table");
        outputTable.addActionListener(createSynzerDataListener());

        inputTable = new JButton("Get TransTable");
        inputTable.addActionListener(createSynzerDataListener());

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
        c.gridwidth = 1;
        message_runPanel.add(new JScrollPane(messageField), c);
//        message_runPanel.add(table, c);
        c.weighty = 0.1;
        c.weightx = 0.0;
        c.gridwidth = 1;
        c.gridx = 1;
        message_runPanel.add(inputTable, c);
        c.weighty = 0.1;
        c.weightx = 0.0;
        c.gridwidth = 1;
        c.gridx = 2;
        message_runPanel.add(outputTable, c);
        c.weighty = 0.1;
        c.weightx = 0.0;
        c.gridwidth = 1;
        c.gridx = 3;
        message_runPanel.add(analyseText, c);

//        gridLayoutMsg.setHgap(10);
//        message_runPanel.setBorder(createBorder(10, 10, 10, 10));
        add(message_runPanel, BorderLayout.SOUTH);
    }

    public void clear() {
        TableModel model = new DefaultTableModel();
        table.setModel(model);
        messageField.setText("");
    }

    private ActionListener createLexerProductsListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LexicalAnalyser lexer = MainWindow.getLexer();
                if (lexer.getLexemes().isEmpty())
                    lexer.run(MainWindow.getLexerPanel().getText());
                if (lexer.getExceptions().isEmpty()) {
                    SyntaxAnalyzer synzer;
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
                    } else if (e.getSource() == idnsButton) {
                        String[] columnNames = {"#",
                                "Name",
                                "Type",};
                        Object[][] data = getIdentifiersData(lexer);

                        TableModel model = new DefaultTableModel(data, columnNames) {
                            @Override
                            public boolean isCellEditable(int row, int column) {
                                return false;
                            }
                        };
                        table.setModel(model);
                        table.getTableHeader().setUpdateTableInRealTime(false);
                    } else if (e.getSource() == consButton) {
                        String[] columnNames = {"#",
                                "Type",
                                "Value",};
                        Object[][] data = getConstantsData(lexer);

                        TableModel model = new DefaultTableModel(data, columnNames) {
                            @Override
                            public boolean isCellEditable(int row, int column) {
                                return false;
                            }
                        };
                        table.setModel(model);
                        table.getTableHeader().setUpdateTableInRealTime(false);
                    } else if (e.getSource() == lblsButton) {
                        String[] columnNames = {"#",
                                "Name"};
                        Object[][] data = getLabelsData(lexer);

                        TableModel model = new DefaultTableModel(data, columnNames) {
                            @Override
                            public boolean isCellEditable(int row, int column) {
                                return false;
                            }
                        };
                        table.setModel(model);
                        table.getTableHeader().setUpdateTableInRealTime(false);
                    } else if (e.getSource() == analyseText) {
                        synzer = MainWindow.getSynzer();
                        if (synzer == null) {
                            synzer = new SyntaxAnalyzerRecursive(lexer);
                        } else {
                            synzer.clear();
                        }

                        if (!synzer.run()) {
                            String[] columnNames = {"#", "Exception"};
                            Object[][] data = getSyntaxExceptionsData(synzer);

                            TableModel model = new DefaultTableModel(data, columnNames) {
                                @Override
                                public boolean isCellEditable(int row, int column) {
                                    return false;
                                }
                            };
                            table.setModel(model);
                            table.getTableHeader().setUpdateTableInRealTime(false);
                        } else {
                            messageField.setText("All right");
                        }
                    }

                } else {
                    showLexerExceptions(lexer);
                }
                try {
                    table.getColumnModel().getColumn(0).setMaxWidth(30);
                } catch (IndexOutOfBoundsException ex) {
                }
            }

            private void setColumnWidth() {
                table.getColumnModel().getColumn(0).setPreferredWidth(30);
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
                        l.getName(),
                        l.getCode(),
                        (l.getCode() == LexicalAnalyser.IDN_CODE) ? l.getSpCode() : "",
                        (l.getCode() == LexicalAnalyser.CON_CODE) ? l.getSpCode() : "",
                        (l.getCode() == LexicalAnalyser.LBL_CODE) ? l.getSpCode() : ""}).collect(Collectors.toList());

                Object[][] data = new Object[lexemes.size()][];
                lexemes.toArray(data);
                return data;
            }

            private Object[][] getIdentifiersData(LexicalAnalyser lexer) {
                Stream<Identifier> idnStream = lexer.getIdentifiers().stream();

                AtomicInteger i = new AtomicInteger(1);
                List<Object[]> identifiers = idnStream.map(l -> new Object[]{i.getAndIncrement(),
                        l.getName(),
                        l.getType()}).collect(Collectors.toList());

                Object[][] data = new Object[identifiers.size()][];
                identifiers.toArray(data);
                return data;
            }

            private Object[][] getConstantsData(LexicalAnalyser lexer) {
                Stream<Constant> conStream = lexer.getConstants().stream();

                AtomicInteger i = new AtomicInteger(1);
                List<Object[]> identifiers = conStream.map(l -> new Object[]{i.getAndIncrement(),
                        l.getType(),
                        l.getName()}).collect(Collectors.toList());

                Object[][] data = new Object[identifiers.size()][];
                identifiers.toArray(data);
                return data;
            }

            private Object[][] getLabelsData(LexicalAnalyser lexer) {
                Stream<Label> lblStream = lexer.getLabels().stream();

                AtomicInteger i = new AtomicInteger(1);
                List<Object[]> identifiers = lblStream.map(l -> new Object[]{i.getAndIncrement(),
                        l.getName()}).collect(Collectors.toList());

                Object[][] data = new Object[identifiers.size()][];
                identifiers.toArray(data);
                return data;
            }

            private Object[][] getSyntaxExceptionsData(SyntaxAnalyzer synzer) {
                Stream<String> exceptionStream = synzer.getErrors().stream();
                AtomicInteger i = new AtomicInteger(1);
                List<Object[]> errors = exceptionStream.map(s -> new Object[]{i.getAndIncrement(),
                        s}).collect(Collectors.toList());

                Object[][] data = new Object[errors.size()][];
                errors.toArray(data);
                return data;
            }

        };
    }

    private ActionListener createSynzerDataListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LexicalAnalyser lexer = MainWindow.getLexer();
                if (lexer.getLexemes().isEmpty())
                    lexer.run(MainWindow.getLexerPanel().getText());
                if (lexer.getExceptions().isEmpty()) {
                    SyntaxAnalyzer synzer;
                    if (e.getSource() == outputTable) {
                        synzer = MainWindow.getSynzer();
                        if (synzer == null) {
                            synzer = new SyntaxAnalyzerAutomate(lexer);
                        } else {
                            synzer.clear();
                        }

                        if (synzer instanceof SyntaxAnalyzerAutomate) {
                            if (synzer.run()) {
                                messageField.setText("All right");
                            }
                            String[] columnNames = {"#", "State", "Label", "Stack"};
                            Object[][] data = getOutputTableData((SyntaxAnalyzerAutomate) synzer);

                            TableModel model = new DefaultTableModel(data, columnNames) {
                                @Override
                                public boolean isCellEditable(int row, int column) {
                                    return false;
                                }
                            };
                            table.setModel(model);
                            table.getTableHeader().setUpdateTableInRealTime(false);
                        } else {
                            String error = "Please use a automated method of syntax analyzer";
                            JOptionPane.showMessageDialog(null, error);
                        }

                    }
                    else if (e.getSource() == inputTable) {
                        synzer = MainWindow.getSynzer();
                        if (synzer == null) {
                            synzer = new SyntaxAnalyzerAutomate(lexer);
                        } else {
                            synzer.clear();
                        }

                        if (synzer instanceof SyntaxAnalyzerAutomate) {
                            if (synzer.run()) {
                                messageField.setText("All right");
                            }
                            String[] columnNames = {"#", "State", "Label", "Transition", "Incomparability"};
                            Object[][] data = getInputingTableData((SyntaxAnalyzerAutomate) synzer);

                            TableModel model = new DefaultTableModel(data, columnNames) {
                                @Override
                                public boolean isCellEditable(int row, int column) {
                                    return false;
                                }
                            };
                            table.setModel(model);
                            table.setDefaultRenderer(Object.class, new MultiLineTableCellRenderer());
                            table.getTableHeader().setUpdateTableInRealTime(false);
                        } else {
                            String error = "Please use a automated method of syntax analyzer";
                            JOptionPane.showMessageDialog(null, error);
                        }

                    }

                    table.getColumnModel().getColumn(0).setMaxWidth(30);
                    table.getColumnModel().getColumn(1).setMaxWidth(50);
                    table.getColumnModel().getColumn(2).setMaxWidth(100);

                } else {
                    // show lexical exceptions
                    showLexerExceptions(lexer);
                    table.getColumnModel().getColumn(0).setMaxWidth(30);
                }
            }

            private Object[][] getInputingTableData(SyntaxAnalyzerAutomate synzer) {
                Stream<Map.Entry<Integer, State>> dataStream = synzer.getTransitions().entrySet().stream();
                Map<Integer, State> trans = synzer.getTransitions();
                AtomicInteger i = new AtomicInteger(1);
                List<Object[]> errors = dataStream.map(d -> new Object[]{i.getAndIncrement(),
                        d.getKey(),
//                        d.getValue().getTransitions(),
                        d.getValue().getTransitions(),
                        d.getValue().getTV(),
                        (d.getValue().getIncomparability() == null) ?
                                d.getValue().getIncomparabilityMsg() : d.getValue().getIncomparability()
                }).collect(Collectors.toList());

                Object[][] data = new Object[errors.size()][];
                errors.toArray(data);
                return data;
            }

            private Object[][] getOutputTableData(SyntaxAnalyzerAutomate synzer) {
                Stream<DataTableField> exceptionStream = synzer.getDataTable().stream();
                AtomicInteger i = new AtomicInteger(1);
                List<Object[]> errors = exceptionStream.map(d -> new Object[]{i.getAndIncrement(),
                        d.getState(), d.getLabel(), d.getStack()}).collect(Collectors.toList());

                Object[][] data = new Object[errors.size()][];
                errors.toArray(data);
                return data;
            }
        };
    }

    private void showLexerExceptions(LexicalAnalyser lexer) {
        String[] columnNames = {"#", "Exception"};
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

    private Object[][] getExceptionsData(LexicalAnalyser lexer) {
        Stream<LexicalException> exceptionStream = lexer.getExceptions().stream();
        AtomicInteger i = new AtomicInteger(1);
        List<Object[]> exceptions = exceptionStream.map(e -> new Object[]{i.getAndIncrement(),
                e.getMessage()}).collect(Collectors.toList());

        Object[][] data = new Object[exceptions.size()][];
        exceptions.toArray(data);
        return data;
    }
//    private ActionListener createAnalyseListener() {
//        return new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
////                LexicalAnalyser lexer = MainWindow.getLexer();
////                if (lexer.getLexemes().isEmpty())
////                    lexer.run(MainWindow.getLexerPanel().getText());
//
//
//                SyntaxAnalyzerRecursive synzer = MainWindow.getSynzer();
//                if (synzer == null) {
//                    LexicalAnalyser lexer = MainWindow.getLexer();
//                    if (lexer.getLexemes().isEmpty())
//                        lexer.run(MainWindow.getLexerPanel().getText());
//                    synzer = new SyntaxAnalyzerRecursive(lexer);
//                } else {
//                    synzer.clear();
//                }
//
//                if (!synzer.run()) {
//                    String[] columnNames = {"#", "Exception"};
//                    Object[][] data = getExceptionsData(synzer);
//
//                    TableModel model = new DefaultTableModel(data, columnNames) {
//                        @Override
//                        public boolean isCellEditable(int row, int column) {
//                            return false;
//                        }
//                    };
//                    table.setModel(model);
//                    table.getTableHeader().setUpdateTableInRealTime(false);
//                } else {
//                    messageField.setText("All right");
//                }
//                table.getColumnModel().getColumn(0).setMaxWidth(30);
//            }
//
//            private Object[][] getExceptionsData(SyntaxAnalyzerRecursive synzer) {
//                Stream<String> exceptionStream = synzer.getErrors().stream();
//                AtomicInteger i = new AtomicInteger(1);
//                List<Object[]> errors = exceptionStream.map(s -> new Object[]{i.getAndIncrement(),
//                        s}).collect(Collectors.toList());
//
//                Object[][] data = new Object[errors.size()][];
//                errors.toArray(data);
//                return data;
//            }
//        };
//    }
}
