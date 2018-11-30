package kpi.skarlet.cad.synzer.transition_table;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class State {
    private int name;
    private Map<String, TransitionElems> transitions;
    private String incomparability;

    public State(int name) {
        this.name = name;
        transitions = new HashMap<>() {
            @Override
            public String toString() {
                return this.entrySet()
                        .stream()
                        .map(entry -> entry.getKey() + " = " + entry.getValue())
                        .collect(Collectors.joining(",\n\t\t\t\t\t\t\t", "{", "}"));
            }
        };
    }

    public State(int name, String incomparability) {
        this.name = name;
        this.incomparability = incomparability;
    }

    public void setIncomparability(String incomparability) {
        this.incomparability = incomparability;
    }

    public void add(String labelKey, TransitionElems transitionElems) {
        transitions.put(labelKey, transitionElems);
    }

    public int getName() {
        return name;
    }

    public TransitionElems getTransition(String label) {
        return transitions.get(label);
    }

    public String getIncomparability() {
        return incomparability;
    }

    @Override
    public String toString() {
        return "State {" +
                "name: " + name +
                ",\n\t\t\ttransitions: " + transitions +
                ",\n\t\t\tincomparability: '" + incomparability + '\'' +
                '}';
    }
}
