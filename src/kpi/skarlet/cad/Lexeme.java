package kpi.skarlet.cad;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Lexeme {
    private static List<Lexeme> lexemes = new ArrayList<>();

    private String lexeme;
    private int line;
    private int lexemeCode;
    private Integer lblCode = null;
    private Integer idnCode = null;
    private Integer conCode = null;

    Lexeme(String lex, int code) {
        lexemes.add(this);

        this.lexeme = lex;
        this.lexemeCode = code;
    }

    Lexeme(String lex, int code, int additionCode) {
        this(lex, code);
        if (code == 100) lblCode = additionCode;
        else if (code == 101) idnCode = additionCode;
        else if (code == 102) conCode = additionCode;
    }

    Lexeme(String lex, int line, int code, int additionCode) {
        this(lex, code, additionCode);
        this.line = line;
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
                    0,  // need add
                    lexeme.lexeme,
                    lexeme.lexemeCode,
                    (lexeme.idnCode != null)? lexeme.idnCode: "",
                    (lexeme.conCode != null)? lexeme.conCode: "",
                    (lexeme.lblCode != null)? lexeme.lblCode: "");
            index++;
        }
    }
}
