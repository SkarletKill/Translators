package kpi.skarlet.cad.synzer;

import kpi.skarlet.cad.lexer.LexicalAnalyser;

public class SyntacticAnalyzer {
    private ErrorConstants EC;
    private TerminalSymbols TS;
    private LexicalAnalyser la;
    private int i;

    public SyntacticAnalyzer() {
        this.la = new LexicalAnalyser();
        this.i = 0;
    }

    public static void main(String[] args) {
        SyntacticAnalyzer syntacticAnalyzer = new SyntacticAnalyzer();
    }

    public boolean program(int i) {
        if (adList(i)) {
            if (la.getLexemes().get(i).getName().equals(TS.OPENING_BRACE)) {
                i++;
                if (operatorList(i)) {
                    if (la.getLexemes().get(i).getName().equals(TS.CLOSING_BRACE)) {
                        i++;
                        return true;
                    } else error(EC.MISSING_CLOSING_BRACE);
                } else error(EC.WRONG_OPERATOR_LIST);
            } else error(EC.MISSING_OPENING_BRACE);
        } else error(EC.WRONG_AD_LIST);
        return false;
    }

    private void error(String msg) {
        System.out.println(msg);
    }
}
