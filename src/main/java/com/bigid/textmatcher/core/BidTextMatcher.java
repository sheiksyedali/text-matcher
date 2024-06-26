package com.bigid.textmatcher.core;

import com.bigid.textmatcher.aggregator.BidAggregator;
import com.bigid.textmatcher.data.RawText;
import com.bigid.textmatcher.data.TextOffset;
import com.bigid.textmatcher.matcher.BidMatcher;
import com.bigid.textmatcher.reader.BidFileReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Author: Sheik Syed Ali
 *
 * Core component for text matcher
 *
 */
public class BidTextMatcher {
    private final int RAW_TEXT_QUEUE_CAPACITY = 5000;
    private final BlockingQueue<RawText> rawTextQueue;
    private final BlockingQueue<TextOffset> matchTextQueue;
    private final Map<String, TextOffset> aggregatedTextResults;
    private String filePath;
    private String searchKeywords;

    private CompletableFuture<Void> fileReadingFuture;
    private BidFileReader fileReader;
    private ExecutorService executorService;
    private CompletableFuture<Void>[] workers;
    private BidMatcher bidMatcher;
    private BidAggregator bidAggregator;
    private int linesToRead = 1000; //Default 1000 lines to read; max 30000

    private boolean isCaseSensitive = true; //Default case sensitive true

    private int matcherWorkers = 5;//Default 5 workers; max 30;


    public BidTextMatcher(String filePath, String searchKeywords) throws ExecutionException, InterruptedException, TimeoutException {
        rawTextQueue = new LinkedBlockingQueue<>(RAW_TEXT_QUEUE_CAPACITY);
        matchTextQueue = new LinkedBlockingQueue<>(); // No capacity, by default max value of Integer.MAX_VALUE
        aggregatedTextResults = new ConcurrentHashMap<>();

        this.filePath = filePath;
        this.searchKeywords = searchKeywords;
    }

    public BidTextMatcher caseSensitive(boolean isCaseSensitive){
        this.isCaseSensitive = isCaseSensitive;
        return this;
    }

    public BidTextMatcher linesToRead(int linesToRead){
        if(linesToRead>0 && linesToRead<=30000){ //Allowed range 1-30000
            this.linesToRead = linesToRead;
        } else{
            System.out.println("Invalid lines to read. Allowed range [1 - 30000. Default: "+this.linesToRead);
        }
        return this;
    }

    public BidTextMatcher matcherWorkers(int matcherWorkers){
        if(matcherWorkers>0 && matcherWorkers<=30){ //Allowed range 1-30
            this.matcherWorkers = matcherWorkers;
        } else {
            System.out.println("Invalid matcher workers. Allowed range [1 - 30]. Default: "+this.matcherWorkers);
        }
        return this;
    }

    public BidTextMatcher build() throws ExecutionException, InterruptedException, TimeoutException {
        prepare();
        return this;
    }

    /**
     * Prepare the dependencies like reader, matcher and aggregator
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    private void prepare() throws ExecutionException, InterruptedException, TimeoutException {

        Path path = Paths.get(filePath);
        if(!Files.exists(path)) {
            throw new RuntimeException("File not found in the provided path: "+filePath);
        }

        //Prepare search keyword list
        List<String> searchToken = prepareSearchKeywords(searchKeywords);

        //Prepare async-file reader
        fileReader = new BidFileReader(path, rawTextQueue, linesToRead);
        fileReadingFuture = fileReader.getFileReadingFuture();

        //Prepare Matcher workers
        bidMatcher = new BidMatcher(rawTextQueue, matchTextQueue, searchToken, isCaseSensitive, matcherWorkers);
        executorService = bidMatcher.getExecutorService();
        workers = bidMatcher.getWorkers();

        //Prepare Aggregator
        bidAggregator = new BidAggregator(matchTextQueue, aggregatedTextResults);
    }

    /**
     * Start function for text matcher
     * @throws InterruptedException
     */
    public void start() throws InterruptedException {
        CompletableFuture<Void> allProcessingFutures = CompletableFuture.allOf(workers);
        CompletableFuture<Void> allTasks = fileReadingFuture.thenCombine(allProcessingFutures, (fileRead, processing) -> null);
        allTasks.join(); // Wait for all tasks to complete
        executorService.shutdown();

        System.out.println("=> All tasks completed");

        bidAggregator.aggregate();
        bidAggregator.printResults();
    }

    private List<String> prepareSearchKeywords(String searchKeywords){
        return Arrays.stream(searchKeywords.split(",")).collect(Collectors.toList());
    }


}
