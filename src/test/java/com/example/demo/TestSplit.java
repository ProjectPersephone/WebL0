package com.example.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;

import com.example.demo.Sentence;

public class TestSplit {
    @Test
    public void main() throws Exception {
        Sentence S = new Sentence("I BE GOOD");
        assertEquals(S.tt_list.size(),3);
        Split split = new Split(S.tt_list,1);
        assertEquals(1,split.before.size());
        assertEquals(2,split.after.size());
        TypedTree a = split.before.get(0);
    }
}