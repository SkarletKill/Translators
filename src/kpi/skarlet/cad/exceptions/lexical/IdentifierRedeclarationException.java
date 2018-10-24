package kpi.skarlet.cad.exceptions.lexical;

import kpi.skarlet.cad.exceptions.LexicalException;

public class IdentifierRedeclarationException extends LexicalException {
    private String idn;
    private int line;

    public IdentifierRedeclarationException(String lex) {
        this.idn = lex;
    }

    public IdentifierRedeclarationException(String lex, int line) {
        this.idn = lex;
        this.line = line;
    }

    @Override
    public String getMessage() {
        StringBuilder msgBuilder = new StringBuilder();
        if (line != 0) msgBuilder.append("line ")
                .append(line)
                .append(": ");
        msgBuilder.append("Identifier '")
                .append(idn)
                .append("' redeclaration");
        return msgBuilder.toString();
    }
}
