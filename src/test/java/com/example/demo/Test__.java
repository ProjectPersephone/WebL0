package com.example.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.example.demo.__;

public class Test__ {
    public Test__() { super(); }
    private static void f1() {
        // assertEquals (__.one_tab, __.out()); //- System.out.println ("f1 __.out() ='" + __.out() + "'");
        f2();
    }
    private static void f2() {
        // assertEquals (__.one_tab+__.one_tab, __.out()); //- System.out.println ("f2 __.out() ='" + __.out() + "'");
    }
    @Test
    public void main() throws Exception {
        __.reset();
        assertEquals ("", __.out());
        f1();                       //- __.ln ("Test of o__ __o:");
        __.o__();
        // assertEquals (__.one_tab, __.out()); //- __.ln ("This should be indented one more");
        __.__o();                  //- __.ln ("This shouldn't be");
    }
}