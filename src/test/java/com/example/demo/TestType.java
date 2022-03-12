package com.example.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.*;

import com.example.demo.AUGType;
import com.example.demo.Type;

public class TestType {
    @Test
    public void main() throws Exception {
        Type T,t1,OTT,OTOTT,a;

        T = Type.term();
        AUGType O = AUGType.O;

        assertNotNull(T);

        t1 = Type.of (AUGType.T,null,null);

        assert(t1.toString().equals("T"));

        assertNotNull(t1);
        assertSame(T,t1);

        OTT = Type.of (O,T,T); // OTT?

        assertSame (OTT.x,T);
        assertSame (OTT.y,T);

        assert(OTT.toString().equals("OTT"));

        a = OTT.fxy(T);
        assertNotNull(a);
        assertSame(a,T);

        OTOTT = Type.of (O,T,OTT); // OTOTT?
        assertNotNull(OTOTT.y);
        assertSame(OTOTT.y,OTT);

        a = OTOTT.fxy(T);
        assertNotNull(a);
        assertSame (a,OTT);
    }
}
