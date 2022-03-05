package com.example.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.example.demo.Tab;

public class TestTab {
    private static void f1() {
        assertEquals (Tab.one_tab, Tab.out()); //- System.out.println ("f1 Tab.out() ='" + Tab.out() + "'");
        f2();
    }
    private static void f2() {
        assertEquals (Tab.one_tab+Tab.one_tab, Tab.out()); //- System.out.println ("f2 Tab.out() ='" + Tab.out() + "'");
    }
    @Test
    public void main() throws Exception {
        Tab.reset();
        assertEquals ("", Tab.out());
        f1();                       //- Tab.ln ("Test of o__ __o:");
        Tab.o__();
        assertEquals (Tab.one_tab, Tab.out()); //- Tab.ln ("This should be indented one more");
        Tab.__o();                  //- Tab.ln ("This shouldn't be");
    }
}