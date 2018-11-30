package kpi.skarlet.cad.synzer.transition_table;

import kpi.skarlet.cad.constants.InitKeywords;
import kpi.skarlet.cad.constants.TerminalSymbols;
import kpi.skarlet.cad.synzer.ErrorConstants;

import java.util.HashMap;
import java.util.Map;

public class StateFactory {
    private TerminalSymbols TS;
    private ErrorConstants EC;
    private Map<String, Integer> KW = new InitKeywords();
    private Map<Integer, State> states = new HashMap<>();
    private int stateNum;

    public static void main(String[] args) {
        StateFactory sf = new StateFactory();
        sf.init();
    }

    private void init() {
        stateNum = 1;
        states.put(stateNum, new State(stateNum));
        states.get(stateNum).add(KW.get(TS.TYPE_INT), new Elems(2, 11, null));
        states.get(stateNum).add(KW.get(TS.TYPE_FLOAT), new Elems(2, 11, null));
        states.get(stateNum).setIncomparability(EC.WRONG_AD);

        stateNum = 2;
        states.put(stateNum, new State(stateNum));
        states.get(stateNum).add(KW.get(TS.SEMICOLON), new Elems(null, 3, null));

        // ...
    }
}
