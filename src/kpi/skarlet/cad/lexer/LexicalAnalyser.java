package kpi.skarlet.cad.lexer;

import kpi.skarlet.cad.lexer.exceptions.EndOfFileException;
import kpi.skarlet.cad.lexer.exceptions.LexicalException;
import kpi.skarlet.cad.lexer.exceptions.lexical.IdentifierRedeclarationException;
import kpi.skarlet.cad.lexer.exceptions.lexical.IdentifierUsingWithoutDeclarationException;
import kpi.skarlet.cad.lexer.exceptions.lexical.UnexpectedLexemeException;
import kpi.skarlet.cad.lexer.exceptions.lexical.UnknownSymbolException;
import kpi.skarlet.cad.lexer.lexemes.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LexicalAnalyser {
    private static CharacterConstants CC;
    private static BufferedReader br;

    private final Map<String, Integer> keywords = initKeywords();

    private static final String EMPTY_LEX = "";
    private VariableType ACTIVE_TYPE;

    private static final int LBL_CODE = 100;
    private static final int IDN_CODE = 101;
    private static final int CON_CODE = 102;

    private List<Lexeme> lexemes = Lexeme.getList();
    private List<Label> labelList = Label.getList();
    private List<Identifier> identifierList = Identifier.getList();
    private List<Constant> constantList = Constant.getList();
    private List<LexicalException> exceptions = new ArrayList<>();

    private boolean lastConst = false;

    private int LINE_NUMBER = 1;
    private char ch;
    private String lex = "";
    private boolean HAS_TO_READ = true;

    public static void main(String[] args) {
        LexicalAnalyser lexer = new LexicalAnalyser();
        lexer.run();
        System.out.println();
    }

    public boolean run() {
        try {
            br = new BufferedReader(new FileReader("res/program.txt"));

            LexicalAnalyser la = new LexicalAnalyser();
            do {
                la.state1();
            } while (true);
        } catch (IOException e) {
            if (e instanceof EndOfFileException) return true;
            else return false;
        }
    }

    public List<Label> getLabels() {
        return labelList;
    }

    public List<Identifier> getIdentifiers() {
        return identifierList;
    }

    public List<Constant> getConstants() {
        return constantList;
    }

    public List<LexicalException> getExceptions() {
        return exceptions;
    }

    private char nextChar() throws IOException {
        int ch = br.read();
        if (ch == -1) {
            // closing the program
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            StackTraceElement place = stackTrace[2];
            System.out.println("Last state: " + place.getMethodName());
            if (exceptions.isEmpty())
                Lexeme.printTable();

            throw new EndOfFileException(place);
        }
        return this.ch = (char) ch;
    }

    private void state1() throws IOException {
        clearLex();
        if (HAS_TO_READ) ch = nextChar();
//        if (Character.isJavaIdentifierStart(ch)) {
        if (Character.isLetter(ch)) {
            lex += ch;
            nextChar();
            state2();
        } else if (Character.isDigit(ch)) {
            lex += ch;
            nextChar();
            state3();
        } else if (ch == CC.dot) {
            lex += ch;
            nextChar();
            state4();
        } else if (ch == CC.equal) {
            lex += ch;
            nextChar();
            state6();
        } else if (ch == CC.exclamation) {
            lex += ch;
            nextChar();
            state7();
        } else if (ch == CC.more) {
            lex += ch;
            nextChar();
            state8();
        } else if (ch == CC.less) {
            lex += ch;
            nextChar();
            state9();
        } else if (ch == CC.minus) {
            lex += ch;
            nextChar();
            state10();
        } else if (isSingleCharacterSeparator(ch)) {
            lex += ch;
            state5();
        } else if (isWhiteSeparator(ch)) {
            if (ch == '\n') LINE_NUMBER++;
            HAS_TO_READ = true;
            // go to state 1
            return;
        } else {
            exceptions.add(new UnknownSymbolException(ch, LINE_NUMBER));
        }

    }

    private void state2() throws IOException {
        if (Character.isLetterOrDigit(ch)) {
            lex += ch;
            nextChar();
            state2();
        } else if (ch == CC.colon) {
            lex += ch;
            state11();
        } else {
            if (isKeyword(lex)) {
                if (getSpecialLexCode(lex) == 1) ACTIVE_TYPE = VariableType.INT;
                else if (getSpecialLexCode(lex) == 2) ACTIVE_TYPE = VariableType.FLOAT;
                addLex(lex, LexemeType.TERMINAL_SYMBOL);
            } else {
                checkIdentifier(lex);
                addLex(lex, LexemeType.IDENTIFIER);
            }
            HAS_TO_READ = false;
//            state1();
        }
    }

    private void state3() throws IOException {
        if (Character.isDigit(ch)) {
            lex += ch;
            nextChar();
            state3();
        } else if (ch == CC.dot) {
            lex += ch;
            nextChar();
            state12();
        } else {
            addLex(lex, LexemeType.CONSTANT);
            HAS_TO_READ = false;
//            state1();
        }
    }

    private void state4() throws IOException {
        if (Character.isDigit(ch)) {
            lex += ch;
            nextChar();
            state12();
        } else {
            exceptions.add(new UnexpectedLexemeException(lex, LINE_NUMBER));
        }
    }

    private void state5() {
        if (ch == CC.semicolon && ACTIVE_TYPE != null) ACTIVE_TYPE = null;
        addLex(lex, LexemeType.TERMINAL_SYMBOL); // OP
        HAS_TO_READ = true;
//        state1();
    }

    private void state6() {
        if (ch == CC.equal) {
            lex += ch;
            state13();  // ==
        } else {
            addLex(lex, LexemeType.TERMINAL_SYMBOL); // =
            HAS_TO_READ = false;
//            state1();
        }
    }

    private void state7() {
        if (ch == CC.equal) {
            lex += ch;
            state13();  // !=
        } else {
            exceptions.add(new UnexpectedLexemeException(lex, LINE_NUMBER));
            lex = EMPTY_LEX;
        }
    }

    private void state8() {
        if (ch == CC.more) {
            lex += ch;
            state13();  // >>
        } else if (ch == CC.equal) {
            lex += ch;
            state13();  // >=
        } else {
            addLex(lex, LexemeType.TERMINAL_SYMBOL); // >
            HAS_TO_READ = false;
//            state1();
        }
    }

    private void state9() {
        if (ch == CC.less) {
            lex += ch;
            state13();  // <<
        } else if (ch == CC.equal) {
            lex += ch;
            state13();  // <=
        } else {
            addLex(lex, LexemeType.TERMINAL_SYMBOL);     // <
            HAS_TO_READ = false;
//            state1();
        }
    }

    private void state10() throws IOException {
        if (Character.isDigit(ch) && !lastConst) {
            lex += ch;
            nextChar();
            state3();
        } else {
            addLex(lex, LexemeType.TERMINAL_SYMBOL);     // -
            HAS_TO_READ = false;
//            state1()
        }
    }

    private void state11() {
        addLex(lex, LexemeType.LABEL);
        HAS_TO_READ = true;
//        state1();
    }

    private void state12() throws IOException {
        if (Character.isDigit(ch)) {
            lex += ch;
            nextChar();
            state12();
        } else {
            addLex(lex, LexemeType.CONSTANT);
            HAS_TO_READ = false;
//            state1();
        }
    }

    private void state13() {
        addLex(lex, LexemeType.TERMINAL_SYMBOL);
        HAS_TO_READ = true;
//        state1();
    }

    private void clearLex() {
        lex = "";
    }

    private void addLex(String lex, LexemeType lexType) {
        Lexeme lexeme = null;

        if (lexType.equals(LexemeType.TERMINAL_SYMBOL)) {
            lexeme = new Lexeme(lex, LINE_NUMBER, getSpecialLexCode(lex));

        } else if (lexType.equals(LexemeType.LABEL)) {
            lexeme = new Lexeme(lex, LINE_NUMBER, LBL_CODE, getLabelCode(lex));
            if (!Label.isExists(lex)) {
                labelList.add(new Label(lex));
            }

        } else if (lexType.equals(LexemeType.IDENTIFIER)) {
            lexeme = new Lexeme(lex, LINE_NUMBER, IDN_CODE, getIdentifierCode(lex));
            if (!Identifier.isExists(lex)) identifierList.add(new Identifier(ACTIVE_TYPE, lex));

        } else if (lexType.equals(LexemeType.CONSTANT)) {
            lastConst = true;
            lexeme = new Lexeme(lex, LINE_NUMBER, CON_CODE, getConstantCode(lex));
            if (!Constant.isExists(lex)) constantList.add(new Constant(lex));
        } else {
//            throw new UnexpectedLexemeException(lex, LINE_NUMBER);
            System.err.println("ERROR: UNEXPECTED EVENT!");
        }

//        if (lexeme != null) lexemes.add(lexeme);
        checkLastConst();
    }

    private void checkLastConst() {
        int c = Lexeme.getLastCode();
        if (c == 102 || c == 29 || c == 101) lastConst = true;
        else lastConst = false;
    }

    private boolean isWhiteSeparator(char ch) {
        if (Character.isSpaceChar(ch)) return true;
        if (ch == '\t' || ch == '\r' || ch == '\n') return true;
        return false;
    }

    private boolean isSingleCharacterSeparator(char ch) {
        String regex = "[:;,+*/=><{}()]";   // deleted \\-
        String character = "" + ch;
        return character.matches(regex);
    }

    private boolean isKeyword(String lex) {
        return keywords.get(lex) != null;
    }

    // check if there is a lexeme in the list
    private boolean isExists(String lex, List<String> list) {
        return list.indexOf(lex) != -1;
    }

    private Integer getSpecialLexCode(String lex) {
        return keywords.get(lex);
    }

    // returns ordinal number (if exists) / 1 (if not exists) of lexeme from list
    private int getCode(List<String> list, String lex) {
        int code = list.indexOf(lex);
        if (code == -1) code = list.size();
        return code + 1;
    }

    private int getLabelCode(String lex) {
        return Label.getCode(lex);
    }

    private int getIdentifierCode(String lex) {
        return Identifier.getCode(lex);
    }

    private int getConstantCode(String lex) {
        return Constant. getCode(lex);
    }

    private void checkIdentifier(String idn) {
        if (ACTIVE_TYPE != null && Identifier.isExists(idn) && Identifier.get(idn).getType() != null) {
            exceptions.add(new IdentifierRedeclarationException(idn, LINE_NUMBER));
        } else if (ACTIVE_TYPE == null && !Identifier.isExists(idn))
            exceptions.add(new IdentifierUsingWithoutDeclarationException(idn, LINE_NUMBER));
//            else if (ACTIVE_TYPE == null && isExists(idn, identifierList)) // => Використання ідентифікатора
//            else if (ACTIVE_TYPE != null && !isExists(idn, identifierList)) // => Оголошення ідентифікатора
    }

    private static Map<String, Integer> initKeywords() {
        return new HashMap<>() {
            {
                put("int", 1);
                put("float", 2);
                put("for", 3);
                put("to", 4);
                put("step", 5);
                put("goto", 6);
                put("if", 7);
                put("cin", 8);
                put("cout", 9);
                put("not", 10);
                put("and", 11);
                put("or", 12);
                put(",", 13);
                put("=", 14);
                put(">>", 15);
                put("<<", 16);
                put("==", 17);
                put("!=", 18);
                put(">", 19);
                put("<", 20);
                put(">=", 21);
                put("<=", 22);
//                put("^", 23);
                put("*", 24);
                put("/", 25);
                put("+", 26);
                put("-", 27);
                put("(", 28);
                put(")", 29);
                put(":", 30);
                put("{", 31);
                put("}", 32);
                put(";", 33);
//                put("LBL", 100);
//                put("IDN", 101);
//                put("CON", 102);
            }
        };
    }

}