package kpi.skarlet.cad.synzer.transition_table;

public class Elems {
    Integer stackPush = null;
    int nextState;
    String comparability;

    public Elems(Integer stackPush, int nextState, String comparability) {
        this.stackPush = stackPush;
        this.nextState = nextState;
        this.comparability = comparability;
    }

    public Integer getStackPush() {
        return stackPush;
    }

    public int getNextState() {
        return nextState;
    }

    public String getComparability() {
        return comparability;
    }
}
