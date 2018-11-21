package kpi.skarlet.cad.synzer;

import kpi.skarlet.cad.lexer.LexicalAnalyser;
import kpi.skarlet.cad.lexer.VariableType;
import kpi.skarlet.cad.lexer.exceptions.EndOfLexemesException;
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
                if (!inc()) return false;
                if (operatorList()) {
                    if (getCurrentLexeme().equals(TS.CLOSING_BRACE)) {
//                        if (!inc()) return false;
                        return true;
                    } else error(EC.MISSING_CLOSING_BRACE);
                } else error(EC.WRONG_OPERATOR_LIST);
            } else error(EC.MISSING_OPENING_BRACE);
        } else error(EC.WRONG_AD_LIST);
        return false;
    }

    private boolean adList() {
        while (!getCurrentLexeme().equals(TS.OPENING_BRACE)) {
//            if (!inc()) return false;
            if (ad()) {
                if (getCurrentLexeme().equals(TS.SEMICOLON)) {
                    if (!inc()) return false;
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
    }

    private boolean ad() {
        if (_type()) {
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
            if (!inc()) return false;
            if (getCurrentLexeme().equals(TS.SEMICOLON)) {
                return true;
            }
            if (getCurrentLexeme().equals(TS.COMMA)) {
                if (!inc()) return false;
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
            if (!inc()) return false;
            if (operatorList()) {
                if (getCurrentLexeme().equals(TS.CLOSING_BRACE)) {
                    return inc();
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
                if (!inc()) return false;
//                while (getCurrentLexeme() != endings) {
                while (!getCurrentLexeme().equals(TS.CLOSING_BRACE)) {          // !!!
                    if (operator()) {
                        if (getCurrentLexeme().equals(TS.SEMICOLON)) {
                            if (!inc()) return false;
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
            return inc();
        }

        return false;
    }

    private boolean input() {
        if (getCurrentLexeme().equals(TS.INPUT_OPERATOR)) {
            if (!inc()) return false;
            if (getCurrentLexeme().equals(TS.INPUT_JOINT)) {
                if (!inc()) return false;
                if (getCurrentLexemeCode() == 101 || getCurrentLexemeCode() == 102) {
                    if (!inc()) return false;
                    while (getCurrentLexeme().equals(TS.INPUT_JOINT)) {
                        if (!inc()) return false;
                        if (getCurrentLexemeCode() == 101 || getCurrentLexemeCode() == 102) {
                            if (!inc()) return false;
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
            if (!inc()) return false;
            if (getCurrentLexeme().equals(TS.OUTPUT_JOINT)) {
                if (!inc()) return false;
                if (getCurrentLexemeCode() == 101 || getCurrentLexemeCode() == 102) {
                    if (!inc()) return false;
                    while (getCurrentLexeme().equals(TS.OUTPUT_JOINT)) {
                        if (!inc()) return false;
                        if (getCurrentLexemeCode() == 101 || getCurrentLexemeCode() == 102) {
                            if (!inc()) return false;
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
            if (!inc()) return false;
            if (getCurrentLexeme().equals(TS.OPENING_BRACKET)) {
                if (!inc()) return false;
                if (assignment()) {
                    if (getCurrentLexeme().equals(TS.SEMICOLON)) {
                        if (!inc()) return false;
                        if (LE()) {
                            if (getCurrentLexeme().equals(TS.SEMICOLON)) {
                                if (!inc()) return false;
                                if (E()) {
                                    if (getCurrentLexeme().equals(TS.CLOSING_BRACKET)) {
                                        if (!inc()) return false;
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
            if (!inc()) return false;
            if (getCurrentLexeme().equals(TS.OPENING_BRACKET)) {
                if (!inc()) return false;
                if (LE()) {
                    if (getCurrentLexeme().equals(TS.CLOSING_BRACKET)) {
                        if (!inc()) return false;
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
            if (!inc()) return false;
            if (getCurrentLexeme().equals(TS.EQUAL)) {
                if (!inc()) return false;
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
            if (!inc()) return false;
            if (getCurrentLexemeCode() == 100) {
                return inc();
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
                if (!inc()) return false;
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
                if (!inc()) return false;
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
            if (!inc()) return false;
            if (LE()) {
                if (getCurrentLexeme().equals(TS.CLOSING_BRACKET)) {
                    return inc();
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
            if (!inc()) return false;
            // TODO investigate logical expressions
            if (LT()) {
                return true;
            } else {
                error(EC.WRONG_NEGATION_BEFORE_EXPRESSION);
                return false;
            }
        }
        return R();
    }

    private boolean R() {
        if (E()) {
            if (S()) {
                if (!inc()) return false;
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
                if (!inc()) return false;
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
                if (!inc()) return false;
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
            return inc();
        }
        if (getCurrentLexeme().equals(TS.OPENING_BRACKET)) {
            if (!inc()) return false;
            if (E()) {
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

    private boolean inc() {
        if (++i < la.getLexemes().size()) {
            currentLex = la.getLexemes().get(i);        // debug only
            return true;
        } else {
            error(new EndOfLexemesException().getMessage());
            return false;
        }
    }

    private boolean _type() {
        if (getCurrentLexeme().equals(VariableType.INT.toString())) {
            return inc();
        } else if (getCurrentLexeme().equals(VariableType.FLOAT.toString())) {
            return inc();
        } else {
            error(EC.WRONG_TYPE);
            return false;
        }
    }

    private String getCurrentLexeme() {
        return la.getLexemes().get(i).getName();
    }

    private int getCurrentLexemeCode() {
        return la.getLexemes().get(i).getCode();
    }

    private void error(String msg) {
        String message = "Line " + la.getLexemes().get((i < la.getLexemes().size()) ? i : la.getLexemes().size() - 1).getLine() + ": " + msg;
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
