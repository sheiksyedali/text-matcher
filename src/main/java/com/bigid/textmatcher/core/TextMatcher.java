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
 */
public class TextMatcher {
    private final int RAW_TEXT_QUEUE_CAPACITY = 5000;
    private final BlockingQueue<RawText> rawTextQueue;
    private final BlockingQueue<TextOffset> parsedTextQueue;
    private final Map<String, TextOffset> aggregatedTextResults;

    private String filePath;
    private String searchKeywords;

    private CompletableFuture<Void> fileReadingFuture;
    private BidFileReader fileReader;
    private ExecutorService executorService;
    private CompletableFuture<Void>[] workers;
    private BidMatcher bidMatcher;
    private BidAggregator bidAggregator;



    public TextMatcher(String filePath, String searchKeywords) throws ExecutionException, InterruptedException, TimeoutException {
        rawTextQueue = new LinkedBlockingQueue<>(RAW_TEXT_QUEUE_CAPACITY);
        parsedTextQueue = new LinkedBlockingQueue<>(); // No capacity, by default max value of Integer.MAX_VALUE
        aggregatedTextResults = new ConcurrentHashMap<>();

        this.filePath = filePath;
        this.searchKeywords = searchKeywords;

        prepare();

    }
    private void prepare() throws ExecutionException, InterruptedException, TimeoutException {

        Path path = Paths.get(filePath);
        if(!Files.exists(path)){
            throw new RuntimeException("File not found in the provided path: "+filePath);
        }

        //Prepare search keyword list
        List<String> searchToken = prepareSearchKeywords(searchKeywords);

        //Prepare async-file reader
        fileReader = new BidFileReader(path, rawTextQueue);
        fileReadingFuture = fileReader.getFileReadingFuture();

        //Prepare Matcher workers
        bidMatcher = new BidMatcher(rawTextQueue, parsedTextQueue, searchToken);
        executorService = bidMatcher.getExecutorService();
        workers = bidMatcher.getWorkers();

        //Prepare Aggregator
        bidAggregator = new BidAggregator();

    }

    public void start(){
        CompletableFuture<Void> allProcessingFutures = CompletableFuture.allOf(workers);
        CompletableFuture<Void> allTasks = fileReadingFuture.thenCombine(allProcessingFutures, (fileRead, processing) -> null);
        allTasks.join(); // Wait for all tasks to complete
        executorService.shutdown();

        System.out.println("All tasks completed");

        bidAggregator.aggregate();
        bidAggregator.printResults();
    }

    private List<String> prepareSearchKeywords(String searchKeywords){
        return Arrays.stream(searchKeywords.split(",")).collect(Collectors.toList());
    }


}
