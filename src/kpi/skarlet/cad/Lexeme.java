package kpi.skarlet.cad;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Lexeme {
    private static List<Lexeme> lexemes = new ArrayList<>();

    private String lexeme;
    private int line;
    private int lexemeCode;
    private Integer spCode = null;

    Lexeme(String lex, int line, int code) {
        lexemes.add(this);

        this.lexeme = lex;
        this.line = line;
        this.lexemeCode = code;
    }

    Lexeme(String lex, int line, int code, int additionCode) {
        this(lex, line, code);
        spCode = additionCode;
    }

    public List<Lexeme> getList() {
        return lexemes;
    }

    /**
     * @return class Lexeme instance (if exists) / null
     */
    public static Lexeme get(String lex) {
        try {
            return lexemes.stream().filter(l -> lex.equals(l.lexeme)).findAny().get();
        } catch (NoSuchElementException ex) {
            return null;
        }
    }

    public static int getLastCode() {
        if (lexemes.isEmpty()) return 0;
        return lexemes.get(lexemes.size() - 1).lexemeCode;
    }

    public static void printTable() {
        if (lexemes.isEmpty()) {
            System.out.println("No lexemes found");
            return;
        }

        System.out.printf("%4s | %7s | %10s | %8s | %8s | %8s | %8s\r\n",
                "#",
                "# рядка",
                "Лексема",
                "LEX code",
                "IDN code",
                "CON code",
                "LBL code");

        int index = 1;
        for (Lexeme lexeme : lexemes) {
            System.out.printf("%4d | %7d | %10s | %8d | %8s | %8s | %8s\r\n",
                    index,
                    lexeme.line,  // need add
                    lexeme.lexeme,
                    lexeme.lexemeCode,
//                    (lexeme.idnCode != null) ? lexeme.idnCode : "",
                    (lexeme.lexemeCode != 101 || lexeme.spCode == null) ? "" : lexeme.spCode,
                    (lexeme.lexemeCode != 102 || lexeme.spCode == null) ? "" : lexeme.spCode,
                    (lexeme.lexemeCode != 100 || lexeme.spCode == null) ? "" : lexeme.spCode);
            index++;
        }
    }
}
