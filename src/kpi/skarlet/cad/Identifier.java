package kpi.skarlet.cad;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class Identifier {
    private static ArrayList<Identifier> idnList = new ArrayList<>();

    private VariableType type;
    private String name;

    public Identifier(VariableType type, String name) {
        this.type = type;
        this.name = name;
    }

    public VariableType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static boolean isExists(String idn) {
        return Identifier.get(idn) != null;
    }

    public static int getCode(String idn) {
        ArrayList<String> idnNamesList = idnList.stream().map(id -> id.name).collect(Collectors.toCollection(ArrayList::new));
        int code = idnNamesList.indexOf(idn);
        if (code == -1) code = idnList.size();
        return code + 1;
    }

    public static ArrayList<Identifier> getIdnList() {
        return idnList;
    }

    /**
     * @return class Lexeme instance (if exists) / null
     */
    public static Identifier get(String lex) {
        try {
            return idnList.stream().filter(l -> lex.equals(l.name)).findAny().get();
        } catch (NoSuchElementException ex) {
            return null;
        }
    }
}
