package kpi.skarlet.cad.exceptions;

public class UnknownSymbolException extends Exception {
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
        String msg = "Unexpected character " + symbol;
        if (line == 0) msg += " in line: " + line;
        return msg;
    }
}
