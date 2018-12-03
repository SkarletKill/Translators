package kpi.skarlet.cad.synzer;

import kpi.skarlet.cad.lexer.LexicalAnalyser;
import kpi.skarlet.cad.lexer.lexemes.Lexeme;
import kpi.skarlet.cad.synzer.transition_table.State;
import kpi.skarlet.cad.synzer.transition_table.TTReader;
import kpi.skarlet.cad.synzer.transition_table.TransitionElems;

import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

public class SyntaxAnalyzerAutomate {
    private LexicalAnalyser la;
    private Map<Integer, State> stateTransitions;
//    private ArrayList<int, String, int[]>

    private Stack<Integer> stack;
    private int i;
    private int state;
    private String curLex;

    public static void main(String[] args) {
        SyntaxAnalyzerAutomate saa = new SyntaxAnalyzerAutomate();

        boolean res = saa.run();
        System.out.println("SA: " + res);
    }

    public SyntaxAnalyzerAutomate() {
        stack = new Stack<>();
        TTReader ttr = new TTReader("res/transition_table.xml");
        this.la = new LexicalAnalyser();
        this.la.run();
        this.stateTransitions = ttr.getStates();
        this.i = 0;
        this.state = 1;
        this.curLex = getCurrentLexeme();
    }

    public boolean run() {
        while (i < la.getLexemes().size()) {
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
                    if (error != null && error.equals("exit")) {
                        if (stack.empty()) return true;
                        state = stack.pop();
                        continue;
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private boolean hasTransition(String lex) {
        return stateTransitions.get(state).getTransition(lex) != null;
    }

    private boolean hasIncompatibilityTransition() {
        return stateTransitions.get(state).getIncomparability() != null;
    }

    private void nextState(TransitionElems elems) {
        state = elems.getNextState();
        if (elems.getStackPush() != null)
            stack.push(elems.getStackPush());
    }

    private String getCurrentLexeme() {
        Lexeme lexeme = la.getLexemes().get(i);
        if (lexeme.getCode() > 99) {
            if (lexeme.getCode() == 100) {
                return "_LBL";
            } else if (lexeme.getCode() == 101) {
                return "_IDN";
            } else if (lexeme.getCode() == 102) {
                return "_CON";
            }
        }
        return lexeme.getName();
    }

    private boolean inc() {
        if (++i < la.getLexemes().size()) {
            return true;
        } else {
            return false;
        }
    }

    private class DataTable {
        int state;
        String label;
        Stack<Integer> stack;
    }
}
