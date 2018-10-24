package kpi.skarlet.cad.exceptions;

public class LexicalException extends Exception {

    protected void exclamation(){
        System.err.println(this.getMessage());
    }

    @Override
    public String getMessage() {
        String msg = "Lexical exception!";
        return msg;
    }
}
