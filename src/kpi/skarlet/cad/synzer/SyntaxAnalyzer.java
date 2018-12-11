package kpi.skarlet.cad.synzer;

import java.util.ArrayList;

public abstract class SyntaxAnalyzer {
    public abstract ArrayList<String> getErrors();
    public abstract void clear();
    public abstract boolean run();
}
