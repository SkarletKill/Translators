package kpi.skarlet.cad;

import kpi.skarlet.cad.exceptions.UnknownSymbolException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LexicalAnalyser {
    private static CharacterConstants CC;
    private static BufferedReader br;

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
            addLex(lex, LexemeType.IDENTIFIER);
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

    private void state5() throws IOException {
        addLex(lex, LexemeType.TERMINAL_SYBOL); // OP
        HAS_TO_READ = true;
//        state1();
    }

    private void state6() throws IOException {
        if (ch == CC.equal) {
            lex += ch;
            state13();  // ==
        } else {
            addLex(lex, LexemeType.TERMINAL_SYBOL); // =
            HAS_TO_READ = false;
//            state1();
        }
    }

    private void state7() throws IOException {
        if (ch == CC.equal) {
            lex += ch;
            state14();  // !=
        } else {
//            throw new UnexpectedLexemeException(lex);
        }
    }

    private void state8() throws IOException {
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

    private void state9() throws IOException {
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

    private void state10() throws IOException {
    }

    private void state11() throws IOException {
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

    private void state13() throws IOException {
        addLex(lex, LexemeType.TERMINAL_SYBOL);
        HAS_TO_READ = true;
//        state1();
    }

    private void state14() throws IOException {
        addLex(lex, LexemeType.TERMINAL_SYBOL);
        HAS_TO_READ = true;
//        state1();
    }

    private void state15() throws IOException {
        addLex(lex, LexemeType.TERMINAL_SYBOL);
        HAS_TO_READ = true;
//        state1();
    }

    private void state16() throws IOException {
        addLex(lex, LexemeType.TERMINAL_SYBOL);
        HAS_TO_READ = true;
//        state1();
    }

    private void state17() throws IOException {
        addLex(lex, LexemeType.TERMINAL_SYBOL);
        HAS_TO_READ = true;
//        state1();
    }

    private void state18() throws IOException {
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
        } else if (lexType.equals(LexemeType.IDENTIFIER)) {
            new Lexeme(lex, IDN_CODE, getIdentifierCode(lex));
        } else if (lexType.equals(LexemeType.CONSTANT)) {
            new Lexeme(lex, CON_CODE, getConstantCode(lex));
        } else {

        }
    }

    private boolean isWhiteSeparator(char ch) {
        if (Character.isSpaceChar(ch)) return true;
        if(ch == '\t' || ch == '\r' || ch == '\n') return true;
        return false;
    }

    // need testing
    private boolean isSingleCharacterSeparator(char ch) {
        String regex = "[:;,+\\-*/=><{}()]";
        String character = "" + ch;
        return character.matches(regex);
    }

    // it's only emulate a nice-working function
    private boolean checkForKeyword(String lex) {
        return false;
    }

    // need testing
    private boolean isLexExist(String lex) {
        return Lexeme.get(lex) != null;
    }

    // it's only emulate a nice-working function
    private Integer getSpecialLexCode(String lex) {
        return 0;
    }

    // need to check
    private int getCode(List<String> list, String lex) {
        int code = list.indexOf(lex);
        if (code == -1) code = list.size() + 1;
        return code;
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

}
