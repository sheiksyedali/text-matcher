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
    private final BlockingQueue<TextOffset> parsedTextQueue;
    private final List<String> searchToken;
    private final boolean isCaseSensitive;
    private final int WORKER_SIZE = 5;
    private ExecutorService executorService;
    private CompletableFuture<Void>[] workers;

    public BidMatcher(BlockingQueue<RawText> rawTextQueue, BlockingQueue<TextOffset> parsedTextQueue, List<String> searchToken, boolean isCaseSensitive){
        this.rawTextQueue = rawTextQueue;
        this.parsedTextQueue = parsedTextQueue;
        this.searchToken = searchToken;
        this.isCaseSensitive = isCaseSensitive;

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
                    System.out.println("=> Worker task completed");
                    break;
                }
                parseAndQueue(text);
            }catch (InterruptedException ex){
                Thread.currentThread().interrupt();
            }
        }
    }

    private void parseAndQueue(RawText rawText){
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
                    Offset offset = new Offset(startIndex, matcher.start()+1);
                    textOffset.addOffset(offset);

//                    String output = "["+token+"] ["+line+"] ["+(matcher.start()+1)+"] ["+rawText.getStartIndex()+"] ["+rawText.getEndIndex()+"]" ;
//                    System.out.println(output);
                }

                parsedTextQueue.add(textOffset);
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
