package com.example.demo;

import org.junit.jupiter.api.Assertions;

import ch.qos.logback.core.joran.conditional.ElseAction;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.demo.Sentence;
import com.example.demo.TypedTree;
import com.example.demo.Line;
import com.example.demo.Tab;

import java.util.*;

public class NestedLines {   // helpful here to add Exceptions
    LinkedList<Line> lines = new LinkedList<Line>();
    static int last_indent_level = 0;

    public void add_line (int indent_level, TypedTree t) {
        Line rpln = new Line(last_indent_level, t);
        lines.add (rpln);
        last_indent_level = indent_level;
    }

    public void add (TypedTree t) {
        add_line (last_indent_level, t);
    }

    public int indent() {
        return (++last_indent_level);
    }
    public int dedent() {
        return (--last_indent_level);
    }

    private void show_helper(LinkedList<Line> L) {
        for (Line Li : L) {
            Tab.ln (Li.line.str());
            if (Li.block != null && Li.block.size() > 0) {
                Tab.ln ("show_helper: block:");
                show_helper (Li.block);
            }
        }
    }
    public void show() {
        show_helper (lines);
    }

    // note side effect on orig from removeFirst().

    private LinkedList<Line> helper (int level, LinkedList<Line> L) {
        LinkedList<Line> r = new LinkedList<Line>();
        Line last = null;

        Tab.ln ("helper: level="+level);

        while (L.size() != 0) {
            Line Li = L.get(0);
            if (Li.level < level) {
                Tab.ln ("Next line dedented, returning");
                break;
            }
            if (Li.level == level) {
                last = L.removeFirst(); // should == Li
                r.add (Li);
                Tab.ln (" added Li=" + Li.str());
            } else {
                Tab.ln ("Next line indented, descending");
                last.block = helper (Li.level, L);
            }
        }
        return r;
    }

    public void postprocess() {
        Tab.ln ("Entering postprocess():");
        LinkedList<Line> ll = (LinkedList<Line>) lines.clone(); // avoid side effects
        int level = ll.get(0).level;

        LinkedList<Line> r = helper (level, ll);  // ll will get eaten up

        Tab.ln ("Exiting postprocess()");
        lines = r;
    }
}