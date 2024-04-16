package com.bigid.textmatcher.aggregator;

import com.bigid.textmatcher.data.TextOffset;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Author: Sheik Syed Ali
 */
public class BidAggregator {
    private final BlockingQueue<TextOffset> parsedQueue;
    private final Map<String, TextOffset> aggregatedTextResults;

    public BidAggregator(BlockingQueue<TextOffset> parsedQueue, Map<String, TextOffset> aggregatedTextResults) {
        this.parsedQueue = parsedQueue;
        this.aggregatedTextResults = aggregatedTextResults;
    }

    public void aggregate() throws InterruptedException {
        System.out.println("=> Aggregation begins");
        TextOffset textOffset = parsedQueue.poll(1, TimeUnit.SECONDS);
        while (textOffset != null){
            String token = textOffset.getText();

            if(aggregatedTextResults.containsKey(token)){
                aggregatedTextResults.get(token).getOffsets().addAll(textOffset.getOffsets());
            } else {
                aggregatedTextResults.put(token, textOffset);
            }

            textOffset = parsedQueue.poll(1, TimeUnit.SECONDS);
        }
        System.out.println("=> Aggregation completed");

    }

    public void printResults() {
        System.out.println("========================== Results =============================");
        aggregatedTextResults.forEach((key, value) -> System.out.println(key+" --> "+value));
        System.out.println("========================== Result ends=============================");

    }
}
