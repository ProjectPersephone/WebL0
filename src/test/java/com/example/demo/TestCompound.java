package com.example.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;

import com.example.demo.Sentence;
import com.example.demo.TypedTree;
import com.example.demo.Compound;
import com.example.demo.__;

public class TestCompound {
    @Test
    public void main() throws Exception {

        __.reset();
        __.trace(false);
        __.ln ("**** TestCompound ****");

        Compound c = new Compound(Nucleus.BE);
        c.args.add (new Compound(Nucleus.I));
        c.args.add (new Compound(Nucleus.GOOD));

        __.ln ("c=" + c);

        LinkedList<Compound> ua = c.unbound_vars();

        __.ln ("ua=" + ua);

        assertEquals (0, ua.size());

        c.args.clear();
        c.args.add (new Compound(Nucleus.I));
        Compound t = new Compound(Nucleus.THIS);
        c.args.add (t);
        t.args.add (new Compound(Nucleus.SOMEONE));

        __.ln ("c should have " + t);
        ua = c.unbound_vars();
        __.ln ("ua="+ua);
        __.ln ("ua.size()=" + ua.size());
        assertEquals (0, ua.size());

        c.args.clear();
        c.args.add (new Compound (Nucleus.I));
        c.args.add (new Compound (Nucleus.SOMEONE));

        __.ln ("c="+c);

        ua = c.unbound_vars ();
        __.ln ("ua="+ua);
        assertEquals(1,ua.size());


        __.ln ("ua=" + ua);
    }
}
