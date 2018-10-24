package kpi.skarlet.cad.exceptions.lexical;

import kpi.skarlet.cad.exceptions.LexicalException;

public class UnknownSymbolException extends LexicalException {
    private char symbol;
    private int line;

    public UnknownSymbolException(char ch) {
        this.symbol = ch;
    }

    public UnknownSymbolException(char ch, int line) {
        this.symbol = ch;
        this.line = line;
    }

    @Override
    public String getMessage() {
        StringBuilder msgBuilder = new StringBuilder();
        if (line != 0) msgBuilder.append("line ")
                .append(line)
                .append(": ");
        msgBuilder.append("Unexpected character '")
                .append(symbol)
                .append("'");
        return msgBuilder.toString();
    }
}
