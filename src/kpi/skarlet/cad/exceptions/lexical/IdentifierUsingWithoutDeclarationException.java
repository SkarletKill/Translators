package kpi.skarlet.cad.exceptions.lexical;

import kpi.skarlet.cad.exceptions.LexicalException;

public class IdentifierUsingWithoutDeclarationException extends LexicalException {
    private String idn;
    private int line;

    public IdentifierUsingWithoutDeclarationException(String lex, int line) {
        this.idn = lex;
        this.line = line;

        super.exclamation();
    }

    @Override
    public String getMessage() {
        StringBuilder msgBuilder = new StringBuilder();
        if (line != 0) msgBuilder.append("line ")
                .append(line)
                .append(": ");
        msgBuilder.append("Identifier '")
                .append(idn)
                .append("' using without declaration");
        return msgBuilder.toString();
    }

}
