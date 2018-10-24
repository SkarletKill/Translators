package kpi.skarlet.cad.exceptions.lexical;

import kpi.skarlet.cad.exceptions.LexicalException;

public class UnknownLexemeTypeException extends LexicalException {
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
        StringBuilder msgBuilder = new StringBuilder();
        if (line != 0) msgBuilder.append("line ")
                .append(line)
                .append(": ");
        msgBuilder.append("Unknown type of lexeme '")
                .append(lex)
                .append("'");
        return msgBuilder.toString();
    }
}
