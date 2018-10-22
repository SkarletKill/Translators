package kpi.skarlet.cad.exceptions;

public class UnexpectedLexemeException extends Exception {
    private String lexeme;
    private int line;

    public UnexpectedLexemeException(String lex) {
        this.lexeme = lex;
    }

    public UnexpectedLexemeException(String lex, int line) {
        this.lexeme = lex;
        this.line = line;
    }

    @Override
    public String getMessage() {
        String msg = "Unexpected lexeme " + lexeme;
        if (line == 0) msg += " in line: " + line;
        return msg;
    }
}
