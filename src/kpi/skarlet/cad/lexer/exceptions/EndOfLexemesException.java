package kpi.skarlet.cad.lexer.exceptions;

public class EndOfLexemesException extends Exception {
    private String message = "End of lexemes reached!";

    public EndOfLexemesException() {}

    @Override
    public String getMessage() {
        return message;
    }
}
