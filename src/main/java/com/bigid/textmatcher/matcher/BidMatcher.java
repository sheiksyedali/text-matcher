package com.bigid.textmatcher.matcher;

import com.bigid.textmatcher.data.Offset;
import com.bigid.textmatcher.data.RawText;
import com.bigid.textmatcher.data.TextOffset;

import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Sheik Syed Ali
 */
public class BidMatcher {
    private final BlockingQueue<RawText> rawTextQueue;
    private final BlockingQueue<TextOffset> matchTextQueue;
    private final List<String> searchToken;
    private final boolean isCaseSensitive;
    private int workerSize = 5; //Default workers 5; max 30;
    private ExecutorService executorService;
    private CompletableFuture<Void>[] workers;

    public BidMatcher(BlockingQueue<RawText> rawTextQueue, BlockingQueue<TextOffset> matchTextQueue, List<String> searchToken, boolean isCaseSensitive, int workerSize){
        this.rawTextQueue = rawTextQueue;
        this.matchTextQueue = matchTextQueue;
        this.searchToken = searchToken;
        this.isCaseSensitive = isCaseSensitive;
        this.workerSize = workerSize;

        prepare();
    }

    /**
     * Prepare the dependencies
     */
    private void prepare(){
        executorService = Executors.newFixedThreadPool(workerSize);
        workers = new CompletableFuture[workerSize];

        for (int i = 0; i < workerSize; i++) {
            workers[i] = CompletableFuture.runAsync(this::process, executorService);
        }
    }

    /**
     * Worker Task
     */
    private void process(){
        while (true) {
            try {
                RawText text = rawTextQueue.poll(1, TimeUnit.SECONDS);
                if(text == null){
                    System.out.println("=> Worker task completed");
                    break;
                }
                matchAndQueue(text);
            }catch (InterruptedException ex){
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Search the token into text if it is matched then put it into matched queue
     * @param rawText
     */
    private void matchAndQueue(RawText rawText){
        int startIndex = rawText.getStarLine();

        Matcher matcher;
        for(String line : rawText.getTexts()) {
            if(!isCaseSensitive){
                line = line.toLowerCase();
            }

            for(String token : searchToken) {
                TextOffset textOffset = new TextOffset(token);

                if(!isCaseSensitive){
                    token = token.toLowerCase();
                }

                Pattern searchPattern = Pattern.compile(token);
                matcher = searchPattern.matcher(line);
                while (matcher.find()) {
                    int index = matcher.start()+1;
                    Offset offset = new Offset(startIndex, index);

                    textOffset.addOffset(offset);
                }

                matchTextQueue.add(textOffset);
            }
            startIndex++;
        }
        System.out.println("=> Matcher-> parsed queue: Batch size: "+rawText.getTexts().size()+"; start line: "+rawText.getStarLine()+"; end line: "+rawText.getEndLine());
    }


    public ExecutorService getExecutorService() {
        return executorService;
    }

    public CompletableFuture<Void>[] getWorkers() {
        return workers;
    }
}
