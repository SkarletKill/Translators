package kpi.skarlet.cad.lexer.lexemes;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class Constant {
    private static List<Constant> conList = new ArrayList<>();

    private String name;

    public Constant(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * @return class Lexeme instance (if exists) / null
     */
    public static Constant get(String lex) {
        try {
            return conList.stream().filter(l -> lex.equals(l.name)).findAny().get();
        } catch (NoSuchElementException ex) {
            return null;
        }
    }

    public static boolean isExists(String lbl) {
        return Label.get(lbl) != null;
    }

    public static int getCode(String con) {
        ArrayList<String> conNamesList = conList.stream().map(id -> id.name).collect(Collectors.toCollection(ArrayList::new));
        int code = conNamesList.indexOf(con);
        if (code == -1) code = conList.size();
        return code + 1;
    }

    public static List<Constant> getList() {
        return conList;
    }
}
