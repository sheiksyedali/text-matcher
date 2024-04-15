package com.bigid.textmatcher.matcher;

import com.bigid.textmatcher.data.RawText;
import com.bigid.textmatcher.data.TextOffset;

import java.util.List;
import java.util.concurrent.*;

/**
 * Author: Sheik Syed Ali
 */
public class BidMatcher {
    private BlockingQueue<RawText> rawTextQueue;
    private BlockingQueue<TextOffset> parsedTextQueue;

    private List<String> searchToken;
    private final int WORKER_SIZE = 5;
    private ExecutorService executorService;
    private CompletableFuture<Void>[] workers;

    public BidMatcher(BlockingQueue<RawText> rawTextQueue, BlockingQueue<TextOffset> parsedTextQueue, List<String> searchToken){
        this.rawTextQueue = rawTextQueue;
        this.parsedTextQueue = parsedTextQueue;
        this.searchToken = searchToken;

        prepare();
    }

    private void prepare(){
        executorService = Executors.newFixedThreadPool(WORKER_SIZE);
        workers = new CompletableFuture[WORKER_SIZE];

        for (int i = 0; i < workers.length; i++) {
            workers[i] = CompletableFuture.runAsync(this::process, executorService);
        }
    }

    private void process(){
        while (true) {
            try {
                RawText text = rawTextQueue.poll(1, TimeUnit.SECONDS); // Poll with timeout
                if(text == null){
                    System.out.println("Null recived");
                    break;
                }
                parseAndQueue(text);
            }catch (InterruptedException ex){
                Thread.currentThread().interrupt();
            }
        }
    }

    private void parseAndQueue(RawText rawText){
//        for(String token : searchToken){
//
//        }
        System.out.println("==========================================");
        rawText.getTexts().stream().forEach(s -> System.out.println(s +" ["+ rawText.getStartIndex()+", "+ rawText.getEndIndex()+"]"));
        System.out.println("==========================================");
    }


    public ExecutorService getExecutorService() {
        return executorService;
    }

    public CompletableFuture<Void>[] getWorkers() {
        return workers;
    }
}
