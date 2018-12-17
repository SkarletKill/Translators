package kpi.skarlet.cad.synzer.transition_table;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class State {
    private int name;
    private Map<String, TransitionElems> transitions;
    private TransitionElems incomparability;
    private String incomparabilityMsg;
    private boolean isFinite = false;

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

    public State(int name, String incomparabilityMsg) {
        this.name = name;
        this.incomparabilityMsg = incomparabilityMsg;
    }

    public void setIncomparabilityMsg(String incomparabilityMsg) {
        this.incomparabilityMsg = incomparabilityMsg;
    }

    public void setIncomparability(TransitionElems transitionElems) {
        this.incomparability = transitionElems;
    }

    public void setFinite(){
        this.isFinite = true;
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

    public String getTransitions() {
        return transitions.keySet().toString();
//        return transitions.entrySet().;

//        Set<Map.Entry<String, TransitionElems>> entries = transitions.entrySet();
//        String keys = "";
//        for (Map.Entry<String, TransitionElems> entry : entries) {
//            keys += entry.getKey() + "\n";
//        }
//        return keys;
    }

    public String getTV(){
        return transitions.values().toString();
//        Collection<TransitionElems> values = transitions.values();

//        Set<Map.Entry<String, TransitionElems>> entries = transitions.entrySet();
//        String vals = "";
//        for (Map.Entry<String, TransitionElems> entry : entries) {
//            vals += entry.getValue() + "\n";
//        }
//        return vals;
    }

    public String getIncomparabilityMsg() {
        return incomparabilityMsg;
    }

    public TransitionElems getIncomparability() {
        return incomparability;
    }

    public boolean isFinite() {
        return isFinite;
    }

    @Override
    public String toString() {
        String space = "\t\t\t";
//        return "State {" +
//                "name: " + name +
//                ",\n" + space + "transitions: " + transitions +
//                ",\n" + space + "incomparabilityMsg: '" + incomparabilityMsg + '\'' +
//                ",\n" + space + "incomparability: " + incomparability +
//                '}';
        return new StringBuilder().append("State {")
                .append("name: ")
                .append(name)
                .append(",\n").append(space).append("transitions: ").append(transitions)
                .append(",\n").append(space).append("incomparabilityMsg: '").append(incomparabilityMsg).append('\'')
                .append(",\n").append(space).append("incomparability: ").append(incomparability)
                .append(",\n").append(space).append("isFinite: ").append(isFinite)
                .append('}')
                .toString();
    }
}
