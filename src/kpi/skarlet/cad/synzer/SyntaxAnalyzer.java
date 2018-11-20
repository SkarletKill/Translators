package kpi.skarlet.cad.synzer;

import kpi.skarlet.cad.lexer.LexicalAnalyser;
import kpi.skarlet.cad.lexer.VariableType;
import kpi.skarlet.cad.lexer.lexemes.Lexeme;

import java.util.ArrayList;

public class SyntaxAnalyzer {
    private ErrorConstants EC;
    private TerminalSymbols TS;
    private ArrayList<String> errors;
    private LexicalAnalyser la;
    private int i;

    private Lexeme currentLex;      // debug only

    public SyntaxAnalyzer(LexicalAnalyser lexer) {
        this.la = lexer;
        this.errors = new ArrayList<>();
        this.i = 0;
    }

    public static void main(String[] args) {
        SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(new LexicalAnalyser());
    }

    public boolean run() {
        return program();
    }

    public boolean program() {
        if (adList()) {
            if (getCurrentLexeme().equals(TS.OPENING_BRACE)) {
                inc();
                if (operatorList()) {
                    if (getCurrentLexeme().equals(TS.CLOSING_BRACE)) {
                        inc();
                        return true;
                    } else error(EC.MISSING_CLOSING_BRACE);
                } else error(EC.WRONG_OPERATOR_LIST);
            } else error(EC.MISSING_OPENING_BRACE);
        } else error(EC.WRONG_AD_LIST);
        return false;
    }

    private boolean adList() {
        while (!getCurrentLexeme().equals(TS.OPENING_BRACE)) {
//            inc();
            if (ad()) {
                if (getCurrentLexeme().equals(TS.SEMICOLON)) {
                    inc();
                } else {
                    error(EC.MISSING_SEMICOLON);
                    return false;
                }
            } else {
                error(EC.MISSING_AD);
                return false;
            }
        }
        return true;

//        if (ad()) {
//            while (getCurrentLexeme().equals(TS.SEMICOLON)) {
//                inc();
//                if (ad()) {
//                    if (getCurrentLexeme().equals(TS.SEMICOLON)) {
//                        inc();
//                    } else {
//                        error(EC.MISSING_SEMICOLON);
//                        return false;
//                    }
//                } else {
//                    error(EC.MISSING_AD);
//                    return false;
//                }
//            }
//            return true;
//        } else {
//            error(EC.MISSING_AD_FIRST);
//            return false;
//        }
    }

    private boolean ad() {
        if (getCurrentLexeme().equals(TS.TYPE_INT) || getCurrentLexeme().equals(TS.TYPE_FLOAT)) {
            inc();
            if (idList()) {
                return true;
            } else {
                error(EC.MISSING_IDENTIFIER);
            }
        } else {
            error(EC.EXPECTED_TYPE);
        }
        return false;
    }

    private boolean idList() {
        if (getCurrentLexemeCode() == 101) {
            inc();
            if (getCurrentLexeme().equals(TS.SEMICOLON)) {
                return true;
            }
            if (getCurrentLexeme().equals(TS.COMMA)) {
                inc();
                if (idList()) {
                    return true;
                } else {
                    error(EC.WRONG_ID_LIST);
                }
            } else {
                error(EC.MISSING_IDLIST_COMMA);
            }
        } else {
            error(EC.MISSING_IDLIST_FIRST_ID);
        }
        return false;
    }

    private boolean statementBlock() {
        if (getCurrentLexeme().equals(TS.OPENING_BRACE)) {
            inc();
            if (operatorList()) {
//                inc();
                if (getCurrentLexeme().equals(TS.CLOSING_BRACE)) {
                    inc();
                    return true;
                } else {
                    error(EC.MISSING_CLOSING_BRACE);
                }
            } else {
                error(EC.WRONG_OPERATOR_LIST);
            }
        } else {
            error(EC.MISSING_OPENING_BRACE);
        }
        return false;
    }

    private boolean operatorList() {
        if (operator()) {
            if (getCurrentLexeme().equals(TS.SEMICOLON)) {
                inc();
//                while (getCurrentLexeme() != endings) {
                while (!getCurrentLexeme().equals(TS.CLOSING_BRACE)) {          // !!!
                    if (operator()) {
                        if (getCurrentLexeme().equals(TS.SEMICOLON)) {
                            inc();
                        } else {
                            error(EC.MISSING_SEMICOLON);
                            return false;
                        }
                    } else {
                        error(EC.MISSING_OPERATOR);
                        return false;
                    }
                }
            } else {
                error(EC.MISSING_SEMICOLON_AFTER_OP);
                return false;
            }
        } else {
            error(EC.MISSING_OPERATOR_FIRST);
            return false;
        }
        return true;
    }

    private boolean operator() {
        if (getCurrentLexeme().equals(TS.INPUT_OPERATOR)) {
            if (input()) {
                return true;
            } else {
                error(EC.WRONG_INPUT);
                return false;
            }
        }

        if (getCurrentLexeme().equals(TS.OUTPUT_OPERATOR)) {
            if (output()) {
                return true;
            } else {
                error(EC.WRONG_OUTPUT);
                return false;
            }
        }

        if (getCurrentLexeme().equals(TS.CYCLE_FOR)) {
            if (loop()) {
                return true;
            } else {
                error(EC.WRONG_LOOP);
                return false;
            }
        }

        if (getCurrentLexeme().equals(TS.CONDITIONAL_OPERATOR)) {
            if (conditional()) {
                return true;
            } else {
                error(EC.WRONG_CONDITIONAL);
                return false;
            }
        }

        if (getCurrentLexemeCode() == 101) {
            if (assignment()) {
                return true;
            } else {
                error(EC.WRONG_ASSIGNMENT);
                return false;
            }
        }

        if (getCurrentLexeme().equals(TS.LABEL_START)) {
            if (labelCall()) {
                return true;
            } else {
                error(EC.WRONG_LABEL);
                return false;
            }
        }

        if (getCurrentLexemeCode() == 100) {
            inc();
            return true;
        }

        return false;
    }

    private boolean input() {
        if (getCurrentLexeme().equals(TS.INPUT_OPERATOR)) {
            inc();
            if (getCurrentLexeme().equals(TS.INPUT_JOINT)) {
                inc();
                if (getCurrentLexemeCode() == 101 || getCurrentLexemeCode() == 102) {
                    inc();
                    while (getCurrentLexeme().equals(TS.INPUT_JOINT)) {
                        inc();
                        if (getCurrentLexemeCode() == 101 || getCurrentLexemeCode() == 102) {
                            inc();
                        } else {
                            error(EC.EXPECTED_INPUT_VARIABLE);
                        }
                    }
                    return true;
                } else {
                    error(EC.EXPECTED_INPUT_VARIABLE);
                }
            } else {
                error(EC.EXPECTED_INPUT_JOINT);
            }
        } else {
            error(EC.EXPECTED_INPUT_OPERATOR);
        }
        return false;
    }

    private boolean output() {
        if (getCurrentLexeme().equals(TS.OUTPUT_OPERATOR)) {
            inc();
            if (getCurrentLexeme().equals(TS.OUTPUT_JOINT)) {
                inc();
                if (getCurrentLexemeCode() == 101 || getCurrentLexemeCode() == 102) {
                    inc();
                    while (getCurrentLexeme().equals(TS.OUTPUT_JOINT)) {
                        inc();
                        if (getCurrentLexemeCode() == 101 || getCurrentLexemeCode() == 102) {
                            inc();
                        } else {
                            error(EC.EXPECTED_OUTPUT_VARIABLE);
                        }
                    }
                    return true;
                } else {
                    error(EC.EXPECTED_OUTPUT_VARIABLE);
                }
            } else {
                error(EC.EXPECTED_OUTPUT_JOINT);
            }
        } else {
            error(EC.EXPECTED_OUTPUT_OPERATOR);
        }
        return false;
    }

    private boolean loop() {
        if (getCurrentLexeme().equals(TS.CYCLE_FOR)) {
            inc();
            if (getCurrentLexeme().equals(TS.OPENING_BRACKET)) {
                inc();
                if (assignment()) {
                    inc();
                    if (getCurrentLexeme().equals(TS.SEMICOLON)) {
                        inc();
                        if (LE()) {
                            inc();
                            if (getCurrentLexeme().equals(TS.SEMICOLON)) {
                                inc();
                                if (E()) {
//                                    inc();
                                    if (getCurrentLexeme().equals(TS.CLOSING_BRACKET)) {
                                        inc();
                                        if (statementBlock()) {
                                            return true;
                                        } else {
                                            error(EC.WRONG_STATEMENT_BLOCK);
                                            return false;
                                        }
                                    } else {
                                        error(EC.MISSING_CLOSING_BRACKET);
                                        return false;
                                    }
                                } else {
                                    error(EC.EXPECTED_EXPRESSION);
                                    return false;
                                }
                            } else {
                                error(EC.EXPECTED_SEMICOLON);
                                return false;
                            }
                        } else {
                            error(EC.EXPECTED_LOOP_LE);
                            return false;
                        }
                    } else {
                        error(EC.EXPECTED_SEMICOLON);
                        return false;
                    }
                } else {
                    error(EC.EXPECTED_LOOP_ASSIGNMENT);
                    return false;
                }
            } else {
                error(EC.MISSING_OPENING_BRACKET);
                return false;
            }
        } else {
            error(EC.WRONG_LOOP);
            return false;
        }
    }

    private boolean conditional() {
        if (getCurrentLexeme().equals(TS.CONDITIONAL_OPERATOR)) {
            inc();
            if (getCurrentLexeme().equals(TS.OPENING_BRACKET)) {
                inc();
                if (LE()) {
//                    inc();
                    if (getCurrentLexeme().equals(TS.CLOSING_BRACKET)) {
                        inc();
                        if (statementBlock()) {
                            return true;
                        } else {
                            error(EC.WRONG_STATEMENT_BLOCK);
                        }
                    } else {
                        error(EC.MISSING_CLOSING_BRACKET);
                    }
                } else {
                    error(EC.WRONG_LE);
                }
            } else {
                error(EC.MISSING_OPENING_BRACKET);
            }
        } else {
            error(EC.WRONG_CONDITIONAL);
        }
        return false;
    }

    private boolean assignment() {
        if (getCurrentLexemeCode() == 101) {
            inc();
            if (getCurrentLexeme().equals(TS.EQUAL)) {
                inc();
                if (E()) {
                    return true;
                } else {
                    error(EC.WRONG_E);
                }
            } else {
                error(EC.MISSING_EQUAL);
            }
        } else {
            error(EC.MISSING_IDENTIFIER);
        }
        return false;
    }

    private boolean labelCall() {
        if (getCurrentLexeme().equals(TS.LABEL_START)) {
            inc();
            if (getCurrentLexemeCode() == 100) {
                inc();
                return true;
            } else {
                error(EC.EXPECTED_LABEL);
            }
        } else {
            error(EC.WRONG_LABEL);
        }
        return false;
    }

    private boolean LE() {
        if (LT()) {
            while (getCurrentLexeme().equals(TS.OR)) {
                inc();
                if (LT()) {
                    return true;
                } else {
                    error(EC.WRONG_LT_SECOND);
                }
            }
            return true;
        } else {
            error(EC.WRONG_LT_FIRST);
        }
        return false;
    }

    private boolean LT() {
        if (LF()) {
            while (getCurrentLexeme().equals(TS.AND)) {
                inc();
                if (LF()) {
                    return true;
                } else {
                    error(EC.WRONG_LF_SECOND);
                }
            }
            return true;
        } else {
            error(EC.WRONG_LF_FIRST);
        }
        return false;
    }

    private boolean LF() {
        if (getCurrentLexeme().equals(TS.OPENING_BRACKET)) {
            inc();
            if (LE()) {
                inc();
                if (getCurrentLexeme().equals(TS.CLOSING_BRACKET)) {
                    inc();
                    return true;
                } else {
                    error(EC.MISSING_CLOSING_BRACKET);
                    return false;
                }
            } else {
                error(EC.WRONG_LE);     // inner
                return false;
            }
        }
        if (getCurrentLexeme().equals(TS.NEGATION)) {
            inc();
            // TODO investigate logical expressions
            if (LT()) {
                return true;
            } else {
                error(EC.WRONG_NEGATION_BEFORE_EXPRESSION);
                return false;
            }
        }
        if (R()) {
            return true;
        }
        return false;
    }

    private boolean R() {
        if (E()) {
//            inc();
            if (S()) {
                inc();
                if (E()) {
                    return true;
                } else {
                    error(EC.WRONG_E_SECOND);
                }
            } else {
                error(EC.WRONG_SIGN);
            }
        } else {
            error(EC.WRONG_E_FIRST);
        }
        return false;
    }

    private boolean S() {
        return getCurrentLexemeCode() >= 17 && getCurrentLexemeCode() <= 22;
    }

    private boolean E() {
        if (T()) {
            while (getCurrentLexeme().equals(TS.PLUS) || getCurrentLexeme().equals(TS.MINUS)) {
                inc();
                if (T()) {
                    continue;
                } else {
                    error(EC.WRONG_T_SECOND);
                }
            }
            return true;
        } else {
            error(EC.WRONG_T_FIRST);
        }
        return false;
    }

    private boolean T() {
        if (V()) {
            while (getCurrentLexeme().equals(TS.ASTERISK) || getCurrentLexeme().equals(TS.SLASH)) {
                inc();
                if (V()) {
                    continue;
                } else {
                    error(EC.WRONG_V_SECOND);
                }
            }
            return true;
        } else {
            error(EC.WRONG_V_FIRST);
        }
        return false;
    }

    private boolean V() {
        if (getCurrentLexemeCode() == 101 || getCurrentLexemeCode() == 102) {
            inc();
            return true;
        }
        if (getCurrentLexeme().equals(TS.OPENING_BRACKET)) {
            inc();
            if (E()) {
//                inc();
                if (getCurrentLexeme().equals(TS.CLOSING_BRACKET)) {
                    return true;
                } else {
                    error(EC.MISSING_CLOSING_BRACKET);
                    return false;
                }
            } else {
                error(EC.WRONG_E);
                return false;
            }
        }
        error(EC.WRONG_V);
        return false;
    }


    private int inc() {
        if (++i < la.getLexemes().size())
            return i;
        else return i;
//        throw new EndOfLexemesException();
    }

    private boolean _type() {
        if (getCurrentLexeme().equals(VariableType.INT)) {
            inc();
            return true;
        } else if (getCurrentLexeme().equals(VariableType.FLOAT)) {
            inc();
            return true;
        } else {
            error(EC.WRONG_TYPE);
            return false;
        }
    }

    private String getCurrentLexeme() {
        currentLex = la.getLexemes().get(i);        // debug only
        return la.getLexemes().get(i).getName();
    }

    private int getCurrentLexemeCode() {
        currentLex = la.getLexemes().get(i);        // debug only
        return la.getLexemes().get(i).getCode();
    }

    private void error(String msg) {
        String message = "Line " + la.getLexemes().get(i).getLine() + ": " + msg;
        errors.add(message);
        System.out.println(message);
    }

    public ArrayList<String> getErrors() {
        return errors;
    }

    public void clear() {
        this.errors.clear();
        this.i = 0;
    }
}
