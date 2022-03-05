package com.example.demo;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;

import com.example.demo.TypedTree;
import com.example.demo.NestedLines;

public class Line {
    int level;
    TypedTree line;
    LinkedList<Line> block;  // if null, no block
    
    public Line (int indent_level, TypedTree t) {
        assertFalse (indent_level < 0);
        assertNotNull (t);
        level = indent_level;
        line = t;
        block = null;
    }

    public String str() {
        return "tab: " + level + "line=" + line.str();
    }
}