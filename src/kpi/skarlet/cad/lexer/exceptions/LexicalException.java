package kpi.skarlet.cad.lexer.exceptions;

public abstract class LexicalException extends Exception {

    protected void exclamation(){
        System.err.println(this.getMessage());
    }

    @Override
    public String getMessage() {
        String msg = "Lexical exception!";
        return msg;
    }
}
