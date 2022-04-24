package com.example.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;

import com.example.demo.Sentence;
import com.example.demo.Splits;

public class TestSplits {
    @Test
    public void main() throws Exception {
        Sentence S = new Sentence("I BE BAD");
        Splits splits = new Splits(S.tt_list);
        assertEquals (2,splits.all_splits.size());
//        S = new Sentence ("a b c");
//        splits = new Splits(S);
//        assertEquals (2,splits.all_splits.size());
    }
}
