package kpi.skarlet.cad.exceptions;

public class LexicalException extends Exception {
    @Override
    public String getMessage() {
        String msg = "Lexical exception!";
        return msg;
    }
}
