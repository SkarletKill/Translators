package kpi.skarlet.cad.synzer;

import kpi.skarlet.cad.lexer.LexicalAnalyser;
import kpi.skarlet.cad.lexer.lexemes.Lexeme;
import kpi.skarlet.cad.synzer.transition_table.DataTableField;
import kpi.skarlet.cad.synzer.transition_table.State;
import kpi.skarlet.cad.synzer.transition_table.TTReader;
import kpi.skarlet.cad.synzer.transition_table.TransitionElems;

import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

public class SyntaxAnalyzerAutomate extends SyntaxAnalyzer {
    public static final String PATH_TRANSITION_TABLE = "res/transition_table.xml";

    private LexicalAnalyser la;
    private Map<Integer, State> stateTransitions;
    private ArrayList<DataTableField> dataTable;
    private ArrayList<String> errors;

    private Stack<Integer> stack;
    private int i;
    private int state;
    private String curLex;

    public static void main(String[] args) {
        SyntaxAnalyzerAutomate saa = new SyntaxAnalyzerAutomate();

        boolean res = saa.run();
        System.out.println("SA: " + res);
//        if (res)
//        System.out.println(saa.dataTable);
        System.out.println(saa.stateTransitions);
    }

    public SyntaxAnalyzerAutomate() {
        dataTable = new ArrayList<>() {
            @Override
            public String toString() {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < this.size(); i++) {
                    builder.append(i + 1).append(": ").append(this.get(i)).append('\n');
                }
                return builder.toString();
            }
        };
        stack = new Stack<>();
        TTReader ttr = new TTReader(PATH_TRANSITION_TABLE);
        this.la = new LexicalAnalyser();
        this.la.run();
        this.stateTransitions = ttr.getStates();
        this.i = 0;
        this.state = 1;
        this.curLex = getCurrentLexeme();
        this.errors = new ArrayList<>();
    }

    public SyntaxAnalyzerAutomate(LexicalAnalyser lexer) {
        dataTable = new ArrayList<>() {
            @Override
            public String toString() {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < this.size(); i++) {
                    builder.append(i + 1).append(": ").append(this.get(i)).append('\n');
                }
                return builder.toString();
            }
        };
        stack = new Stack<>();
        TTReader ttr = new TTReader(PATH_TRANSITION_TABLE);
        this.la = lexer;
//        this.la.run();
        this.stateTransitions = ttr.getStates();
        this.i = 0;
        this.state = 1;
        this.curLex = getCurrentLexeme();
        this.errors = new ArrayList<>();
    }

    @Override
    public boolean run() {
        while (true) {    // || не последняя лксема
            String error;
            TransitionElems elems;
            if (hasTransition(getCurrentLexeme())) {
                elems = stateTransitions.get(state).getTransition(getCurrentLexeme());
                nextState(elems);
                inc();
            } else {
                if (hasIncompatibilityTransition()) {
                    elems = stateTransitions.get(state).getIncomparability();
                    nextState(elems);
                } else {
                    error = stateTransitions.get(state).getIncomparabilityMsg();
                    if (error != null) {
                        if (error.equals("exit")) {
                            if (stack.empty()) {
                                if (i < la.getLexemes().size()) {
                                    error("There is extra code after closing brace. Nothing expected");
                                    return false;
                                }
                                return true;
                            }
                            addTableRecord();
                            state = stack.pop();
                            continue;
                        } else {
                            error(error);
                        }
                        return false;
                    }
                }
            }
        }
//        return false;
    }

    private void error(String error) {
        errors.add("line: " + getLexemeLine(i) + " - " + error + "! But found" + ((curLex.equals("")) ? " nothing" : ": " + curLex));
    }

    @Override
    public ArrayList<String> getErrors() {
        return errors;
    }

    @Override
    public void clear() {
//        this.stateTransitions.clear();
        this.dataTable.clear();
        this.stack.clear();
        this.errors.clear();
        this.i = 0;
        this.state = 1;
        this.curLex = getCurrentLexeme();
    }

    public Map<Integer, State> getTransitions() {
        return stateTransitions;
    }

    public ArrayList<DataTableField> getDataTable() {
        return dataTable;
    }

    private boolean hasTransition(String lex) {
        try {
            return stateTransitions.get(state).getTransition(lex) != null;
        } catch (Exception e){
            System.out.println();
        }
        return false;
    }

    private boolean hasIncompatibilityTransition() {
        return stateTransitions.get(state).getIncomparability() != null;
    }

    private void addTableRecord() {
        dataTable.add(new DataTableField(state, curLex, stackCopy()));
    }

    private void nextState(TransitionElems elems) {
        addTableRecord();
        state = elems.getNextState();
        if (elems.getStackPush() != null)
            stack.push(elems.getStackPush());
    }

    private String getCurrentLexeme() {
        if (i >= la.getLexemes().size()) return curLex = "";
        Lexeme lexeme = la.getLexemes().get(i);
        if (lexeme.getCode() > 99) {
            if (lexeme.getCode() == 100) {
                curLex = lexeme.getName();
                return "_LBL";
            } else if (lexeme.getCode() == 101) {
                curLex = lexeme.getName();
                return "_IDN";
            } else if (lexeme.getCode() == 102) {
                curLex = lexeme.getName();
                return "_CON";
            }
        }
        return curLex = lexeme.getName();
    }

    private int getLexemeLine(int i) {
        if (la.getLexemes().isEmpty()) return 0;
        int index = (i < la.getLexemes().size()) ? i : la.getLexemes().size() - 1;
        return la.getLexemes().get(index).getLine();
    }

    private boolean inc() {
        if (++i < la.getLexemes().size()) {
            return true;
        } else {
            return false;
        }
    }

    private ArrayList<Integer> stackCopy() {
        ArrayList<Integer> list = new ArrayList<>();
        list.addAll(stack);
        return list;
    }

}
