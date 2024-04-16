package com.bigid.textmatcher.core;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Author: Sheik Syed Ali
 */

public class BidTextMatcherTest {
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void testMatcher() throws ExecutionException, InterruptedException, TimeoutException {
        String testFilePath = "bigpart.txt";

        String searchKeywords = "James,Arthur,Ryan,Roger";
        int linesToRead = 1000;
        int matcherWorkers = 3;
        boolean caseSensitive = true;

        BidTextMatcher bidTextMatcher = new BidTextMatcher(testFilePath, searchKeywords)
                .matcherWorkers(matcherWorkers)
                .linesToRead(linesToRead)
                .caseSensitive(caseSensitive)
                .build();
        bidTextMatcher.start();

        String consoleOutput = outputStreamCaptor.toString();
        assertTrue(consoleOutput.contains("Arthur --> [[[lineOffset=2, charOffset=8]"));
    }
}
