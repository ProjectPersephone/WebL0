package com.example.demo;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.*;

import com.example.demo.Type;
import com.example.demo.TypedTree;

public class NSMprolog {
    private static NSMprolog code = null;
    private static Stack<NSMprolog> stack;

    AUGType type;       // maybe an initial "if" is ":-"???
    LinkedList<NSMprolog> args; // if null, not a predicate, just an atom
    
    public NSMprolog(AUGType t) {
        type = t;
    }
}
