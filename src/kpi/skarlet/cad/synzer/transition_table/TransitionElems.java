package kpi.skarlet.cad.synzer.transition_table;

public class TransitionElems {
    private Integer stackPush = null;
    private int nextState;
    private String comparability;

    public TransitionElems(Integer stackPush, int nextState, String comparability) {
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

    @Override
    public String toString() {
        return new StringBuilder().append("{")
                .append("stack: ")
                .append(stackPush)
                .append(", ")
                .append("nextState: ")
                .append(nextState)
                .append("\'}").toString();
    }
}
