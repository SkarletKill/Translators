package kpi.skarlet.cad.exceptions;

public class UnknownLexemeTypeException extends Exception {
    private String lex;
    private int line;

    public UnknownLexemeTypeException(String lex) {
        this.lex = lex;
    }

    public UnknownLexemeTypeException(String lex, int line) {
        this.lex = lex;
        this.line = line;
    }

    @Override
    public String getMessage() {
        String msg = "Unknown type for lexeme: " + lex;
        if (line == 0) msg += " in line: " + line;
        return msg;
    }
}
