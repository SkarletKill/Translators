package kpi.skarlet.cad.synzer;

import kpi.skarlet.cad.lexer.LexicalAnalyser;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow extends JFrame {
    private final int VERTICAL_BORDER = 0;
    private final int HORIZONTAL_BORDER = 10;
//    private final int WIDTH_SCREEN = 1366;
//    private final int HEIGHT_SCREEN = 768;

    LexicalAnalyser lexer;


    private AbstractBorder border = createBorder(VERTICAL_BORDER, HORIZONTAL_BORDER, VERTICAL_BORDER, HORIZONTAL_BORDER);

    JPanel lexerPanel = new LexerPanel();
    JPanel synzerPanel = new SynzerPanel();
    JPanel centralPanel;

    JRadioButton rb_selectProgramText;
    JRadioButton rb_analyzeProgramText;

//    ActionListener radioListener = createNewRBListener();

    public MainWindow() {
        setTitle("Synzer");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
//        setResizable(false);

        setLayout(new BorderLayout());
        init();
        lexer.run();

        setVisible(true);
    }

    public static void main(String[] args) {
        MainWindow mw = new MainWindow();
    }

    private void init() {
        lexer = new LexicalAnalyser();

        rb_selectProgramText = new JRadioButton("Program text entry");
        rb_analyzeProgramText = new JRadioButton("Analyze the program code");
        ButtonGroup radioButtons = new ButtonGroup();
        radioButtons.add(rb_selectProgramText);
        radioButtons.add(rb_analyzeProgramText);
        rb_selectProgramText.addActionListener(createNewRBListener(true, false));
        rb_analyzeProgramText.addActionListener(createNewRBListener(false, true));

        JPanel radioButtonPanel = new JPanel();
        radioButtonPanel.setBorder(border);
        radioButtonPanel.setLayout(new GridLayout(1, 2));
        radioButtonPanel.add(rb_selectProgramText);
        radioButtonPanel.add(rb_analyzeProgramText);
        add(radioButtonPanel, BorderLayout.NORTH);

        centralPanel = lexerPanel;
        add(centralPanel, BorderLayout.CENTER);
    }

    public static CompoundBorder createBorder(int top, int left, int bottom, int right) {
        return new CompoundBorder(new EmptyBorder(top, left, bottom, right), new EmptyBorder(0, 0, 0, 0));
    }

    private ActionListener createNewRBListener(boolean lexerV, boolean synzerV) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (lexerV) {
                    centralPanel = lexerPanel;
                }
                if (synzerV) {
                    centralPanel = synzerPanel;
                }
                remove(centralPanel);
                add(centralPanel);

                lexerPanel.setVisible(lexerV);
                synzerPanel.setVisible(synzerV);
            }
        };
    }
}