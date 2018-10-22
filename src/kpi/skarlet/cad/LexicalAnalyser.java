package kpi.skarlet.cad;

import kpi.skarlet.cad.exceptions.UnexpectedLexemeException;
import kpi.skarlet.cad.exceptions.UnknownLexemeTypeException;
import kpi.skarlet.cad.exceptions.UnknownSymbolException;

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

    private static final Map<String, Integer> keywords = initKeywords();

    private static final String EMPTY_LEX = "";
//    private static String ACTIVE_TYPE = null;

    private static final int LBL_CODE = 100;
    private static final int IDN_CODE = 101;
    private static final int CON_CODE = 102;

    private static ArrayList<String> labelList = new ArrayList<>();
    private static ArrayList<String> identifierList = new ArrayList<>();
    private static ArrayList<String> constantList = new ArrayList<>();

    private static int LEX_CODE = 1;
    private static int LINE_NUMBER = 1;
    private static char ch;
    private static String lex = "";
    private static boolean HAS_TO_READ = true;

//    Function<Function<>, Function<>> call_f = func -> func(nextChar(), lex+ch);

    public static void main(String[] args) {
        try {
            br = new BufferedReader(new FileReader("res/program.txt"));

            LexicalAnalyser la = new LexicalAnalyser();
            do {
                la.state1();
            }
            while (true);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnknownSymbolException e) {
            System.err.println(e.getMessage());
        }
    }

    private char nextChar() throws IOException {
        int ch = br.read();
        if (ch == -1) {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            StackTraceElement place = stackTrace[2];
            System.out.println();
            System.out.println("Last state: " + place.getMethodName());
            Lexeme.printTable();
            System.exit(1);
        }
        return LexicalAnalyser.ch = (char) ch;
    }

    // HAS_TO_READ ?
    private void state1() throws IOException, UnknownSymbolException {
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
        } else if (isSingleCharacterSeparator(ch)) {
            lex += ch;
            state5();
        } else if (isWhiteSeparator(ch)) {
            if (ch == '\n') LINE_NUMBER++;
            HAS_TO_READ = true;
            // go to state 1
            return;
        } else {
            System.out.println();
            throw new UnknownSymbolException(ch, LINE_NUMBER);
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
            addLex(lex, (isKeyword(lex)) ? LexemeType.TERMINAL_SYBOL : LexemeType.IDENTIFIER);
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
//            throw new UnexpectedLexemeException(lex);
        }
    }

    private void state5() {
        addLex(lex, LexemeType.TERMINAL_SYBOL); // OP
        HAS_TO_READ = true;
//        state1();
    }

    private void state6() {
        if (ch == CC.equal) {
            lex += ch;
            state13();  // ==
        } else {
            addLex(lex, LexemeType.TERMINAL_SYBOL); // =
            HAS_TO_READ = false;
//            state1();
        }
    }

    private void state7() {
        if (ch == CC.equal) {
            lex += ch;
            state14();  // !=
        } else {
//            throw new UnexpectedLexemeException(lex);
        }
    }

    private void state8() {
        if (ch == CC.more) {
            lex += ch;
            state15();  // >>
        } else if (ch == CC.equal) {
            lex += ch;
            state16();  // >=
        } else {
            addLex(lex, LexemeType.TERMINAL_SYBOL); // >
            HAS_TO_READ = false;
//            state1();
        }
    }

    private void state9() {
        if (ch == CC.less) {
            lex += ch;
            state17();  // <<
        } else if (ch == CC.equal) {
            lex += ch;
            state18();  // <=
        } else {
            addLex(lex, LexemeType.TERMINAL_SYBOL);     // <
            HAS_TO_READ = false;
//            state1();
        }
    }

    private void state10() {
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
        addLex(lex, LexemeType.TERMINAL_SYBOL);
        HAS_TO_READ = true;
//        state1();
    }

    private void state14() {
        addLex(lex, LexemeType.TERMINAL_SYBOL);
        HAS_TO_READ = true;
//        state1();
    }

    private void state15() {
        addLex(lex, LexemeType.TERMINAL_SYBOL);
        HAS_TO_READ = true;
//        state1();
    }

    private void state16() {
        addLex(lex, LexemeType.TERMINAL_SYBOL);
        HAS_TO_READ = true;
//        state1();
    }

    private void state17() {
        addLex(lex, LexemeType.TERMINAL_SYBOL);
        HAS_TO_READ = true;
//        state1();
    }

    private void state18() {
        addLex(lex, LexemeType.TERMINAL_SYBOL);
        HAS_TO_READ = true;
//        state1();
    }

    private void clearLex() {
        lex = "";
    }

    // it's only emulate a nice-working function
    private void addLex(String lex, LexemeType lexType) {
        if (lexType.equals(LexemeType.TERMINAL_SYBOL)) {
            new Lexeme(lex, getSpecialLexCode(lex));
        } else if (lexType.equals(LexemeType.LABEL)) {
            new Lexeme(lex, LBL_CODE, getLabelCode(lex));
            if (!isExist(lex, labelList)) labelList.add(lex);
        } else if (lexType.equals(LexemeType.IDENTIFIER)) {
            new Lexeme(lex, IDN_CODE, getIdentifierCode(lex));
            if (!isExist(lex, identifierList)) identifierList.add(lex);
//            keywords.contains("int");
        } else if (lexType.equals(LexemeType.CONSTANT)) {
            new Lexeme(lex, CON_CODE, getConstantCode(lex));
            if (!isExist(lex, constantList)) constantList.add(lex);
        } else {
//            throw new UnexpectedLexemeException(lex, LINE_NUMBER);
        }
    }

    private boolean isWhiteSeparator(char ch) {
        if (Character.isSpaceChar(ch)) return true;
        if (ch == '\t' || ch == '\r' || ch == '\n') return true;
        return false;
    }

    /// need testing
    private boolean isSingleCharacterSeparator(char ch) {
        String regex = "[:;,+\\-*/=><{}()]";
        String character = "" + ch;
        return character.matches(regex);
    }

    private boolean isKeyword(String lex) {
        return keywords.get(lex) != null;
    }

    private Integer getSpecialLexCode(String lex) {
        return keywords.get(lex);
    }

    // check if there is a lexeme in the list
    private boolean isExist(String lex, List<String> list) {
        return list.indexOf(lex) != -1;
    }

    /// need testing
    private boolean isLexExist(String lex) {
        return Lexeme.get(lex) != null;
    }

    // returns ordinal number (if exists) / 1 (if not exists) of lexeme from list
    private int getCode(List<String> list, String lex) {
        int code = list.indexOf(lex);
        if (code == -1) code = list.size();
        return code + 1;
    }

    private int getLabelCode(String lex) {
        return getCode(labelList, lex);
    }

    private int getIdentifierCode(String lex) {
        return getCode(identifierList, lex);
    }

    private int getConstantCode(String lex) {
        return getCode(constantList, lex);
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