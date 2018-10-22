package kpi.skarlet.cad;

import kpi.skarlet.cad.exceptions.UnknownSymbolException;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownServiceException;
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
//                ch = la.nextChar();
//                System.out.print((char) ch);
                la.state1((char) ch, "", false);
            }
            while (true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // need testing
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
        return (char) ch;
    }

    // HAS_TO_READ ?
    private void state1(char ch, String lex, boolean hasToRead) throws IOException{
        if (HAS_TO_READ) ch = nextChar();
//        if (Character.isJavaIdentifierStart(ch)) {
        if (Character.isLetter(ch)) {
            state2(nextChar(), lex + ch);
        } else if (Character.isDigit(ch)) {
            state3(nextChar(), lex + ch);
        } else if (ch == CC.dot) {
            state4(nextChar(), lex + ch);
        } else if (ch == CC.equal) {
            state6(nextChar(), lex + ch);
        } else if (ch == CC.exclamation) {
            state7(nextChar(), lex + ch);
        } else if (ch == CC.more) {
            state8(nextChar(), lex + ch);
        } else if (ch == CC.less) {
            state9(nextChar(), lex + ch);
        } else if (isSingleCharacterSeparator(ch)) {
            state5(ch, lex + ch);
        } else if (Character.isSpaceChar(ch)) {
            if(ch == '\n') LINE_NUMBER++;
            state1(nextChar(), lex, false);
        }
//        else throw new UnknownSymbolException(ch, LINE_NUMBER);

    }

    private void state2(char ch, String lex) throws IOException {
        if (Character.isLetterOrDigit(ch)) {
            state2(nextChar(), lex + ch);
        } else if (ch == CC.colon) {
            state11(ch, lex + ch);
        } else {
            addLex(lex, LexemeType.IDENTIFIER);
            state1(ch, EMPTY_LEX, false);
        }
    }

    private void state3(char ch, String lex) throws IOException {
        if (Character.isDigit(ch)) {
            state3(nextChar(), lex + ch);
        } else if (ch == CC.dot) {
            state12(nextChar(), lex + ch);
        } else {
            addLex(lex, LexemeType.CONSTANT);
            state1(ch, EMPTY_LEX, false);
        }
    }

    private void state4(char ch, String lex) throws IOException {
        if (Character.isDigit(ch)) {
            state12(nextChar(), lex + ch);
        } else {
//            throw new UnexpectedLexemeException(lex);
        }
    }

    private void state5(char ch, String lex) throws IOException {
        addLex(lex, LexemeType.TERMINAL_SYBOL); // OP
        state1(ch, EMPTY_LEX, true);
    }

    private void state6(char ch, String lex) throws IOException {
        if (ch == CC.equal) {
            state13(ch, lex + ch);  // ==
        } else {
            addLex(lex, LexemeType.TERMINAL_SYBOL); // =
            state1(ch, EMPTY_LEX, false);
        }
    }

    private void state7(char ch, String lex) throws IOException {
        if (ch == CC.equal) {
            state14(ch, lex + ch);  // !=
        } else {
//            throw new UnexpectedLexemeException(lex);
        }
    }

    private void state8(char ch, String lex) throws IOException {
        if (ch == CC.more) {
            state15(ch, lex + ch);  // >>
        } else if (ch == CC.equal) {
            state16(ch, lex + ch);  // >=
        } else {
            addLex(lex, LexemeType.TERMINAL_SYBOL); // >
            state1(ch, EMPTY_LEX, false);
        }
    }

    private void state9(char ch, String lex) throws IOException {
        if (ch == CC.less) {
            state17(ch, lex + ch);  // <<
        } else if (ch == CC.equal) {
            state18(ch, lex + ch);  // <=
        } else {
            addLex(lex, LexemeType.TERMINAL_SYBOL);     // <
            state1(ch, EMPTY_LEX, false);
        }
    }

    private void state10(char ch, String lex) throws IOException {
    }

    private void state11(char ch, String lex) throws IOException {
        addLex(lex, LexemeType.LABEL);
        state1(ch, EMPTY_LEX, true);
    }

    private void state12(char ch, String lex) throws IOException {
        if (Character.isDigit(ch)) {
            state12(nextChar(), lex + ch);
        } else {
            addLex(lex, LexemeType.CONSTANT);
            state1(ch, EMPTY_LEX, false);
        }
    }

    private void state13(char ch, String lex) throws IOException {
        addLex(lex, LexemeType.TERMINAL_SYBOL);
        state1(ch, EMPTY_LEX, true);
    }

    private void state14(char ch, String lex) throws IOException {
        addLex(lex, LexemeType.TERMINAL_SYBOL);
        state1(ch, EMPTY_LEX, true);
    }

    private void state15(char ch, String lex) throws IOException {
        addLex(lex, LexemeType.TERMINAL_SYBOL);
        state1(ch, EMPTY_LEX, true);
    }

    private void state16(char ch, String lex) throws IOException {
        addLex(lex, LexemeType.TERMINAL_SYBOL);
        state1(ch, EMPTY_LEX, true);
    }

    private void state17(char ch, String lex) throws IOException {
        addLex(lex, LexemeType.TERMINAL_SYBOL);
        state1(ch, EMPTY_LEX, true);
    }

    private void state18(char ch, String lex) throws IOException {
        addLex(lex, LexemeType.TERMINAL_SYBOL);
        state1(ch, EMPTY_LEX, true);
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

    // need testing
    private boolean isSingleCharacterSeparator(char ch) {
        String regex = "[:;,+\\-*/=><{}]";
        String character = "" + ch;
        return character.matches(regex);
    }

    // it's only emulate a nice-working function
    private boolean checkForKeyword(String lex){
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
