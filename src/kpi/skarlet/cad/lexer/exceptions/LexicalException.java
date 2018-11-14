package kpi.skarlet.cad.lexer.exceptions;

import java.util.ArrayList;
import java.util.List;

public abstract class LexicalException extends Exception {
    private static List<LexicalException> lexicalExceptions = new ArrayList<>();
    protected int line;

    public LexicalException() {
//        lexicalExceptions.add(this);
    }

    protected void exclamation(){
        System.err.println(this.getMessage());
    }

    @Override
    public String getMessage() {
        String msg = "Lexical exception!";
        return msg;
    }

    public int getLine(){
        return this.line;
    }

    public static List<LexicalException> getList() {
        return lexicalExceptions;
    }
}
