package com.example.demo;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.*;

import com.example.demo.AUGType; // for now: S, T, Oxy

public class Type {
    static HashMap<AUGType,Type> uniques = new HashMap<AUGType,Type>();

    AUGType type;

// if type = O:
    Type x;
    Type y;

    public Type fxy(Type q) {
        if (type == AUGType.O && x == q)
            return y;
        return null;
    }

    private Type(AUGType augt, Type one, Type the_other) {
        type = augt;
        x = one;
        y = the_other;
    }

    public String str() {
        String s = this.type.name();

        if (type == AUGType.O) {
            assertNotNull (x);
            assertNotNull (y);
            return s + x.str() + y.str();
        }
        else
            return s;
    }

    static public Type of (AUGType augt, Type t_x, Type t_y) {

        Type t = uniques.get(augt);

        if (t != null && t.x == t_x && t.y == t_y)
            return t;

        Type tr = new Type (augt, t_x, t_y);
        uniques.put (augt, tr);

        return tr;
    }

    static public Type term () {
        return Type.of (AUGType.T,null,null);
    }

    static public Type sentence () {
        return Type.of (AUGType.S,null,null);
    }

    public static String ls_str (LinkedList<Type> l) {
        String s = "{";
        Iterator<Type> ti = l.iterator();
        while (ti.hasNext()) {
            s += ti.next().str();
            if (ti.hasNext())
                s += ",";
        }
        s += "}";

        return s;
    }

    public static String all_types() {
        String s = "all types=";

        for (AUGType k : uniques.keySet()) {
            s += uniques.get(k).str();
        }

        return s;
    }
}