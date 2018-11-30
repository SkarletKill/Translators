package kpi.skarlet.cad.synzer.transition_table;

import java.util.HashMap;
import java.util.Map;

public class State {
    private int name;
    private Map<Integer, TransitionElems> transitions;
    private String incomparability;

    public State(int name) {
        this.name = name;
        transitions = new HashMap<>();
    }

    public State(int name, String incomparability) {
        this.name = name;
        this.incomparability = incomparability;
    }

    public void setIncomparability(String incomparability) {
        this.incomparability = incomparability;
    }

    public void add(Integer labelKey, TransitionElems transitionElems) {
        transitions.put(labelKey, transitionElems);
    }

    public int getName() {
        return name;
    }

    public Map<Integer, TransitionElems> getTransitions() {
        return transitions;
    }

    public String getIncomparability() {
        return incomparability;
    }
}
