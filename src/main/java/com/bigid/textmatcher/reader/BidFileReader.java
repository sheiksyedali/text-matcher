package com.bigid.textmatcher.reader;

import com.bigid.textmatcher.data.RawText;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Author: Sheik Syed Ali
 */
public class BidFileReader {
    private Path path;
    private BlockingQueue<RawText> rawTextQueue;
    private final int linesToRead;
    private long position = 0;
    private ByteBuffer buffer;

    private StringBuilder carryForward;

    private int startLineMarker = 1;
    private int endLineMarker = 0;
    private CompletableFuture<Void> fileReadingFuture;

    public BidFileReader(Path path, BlockingQueue<RawText> rawTextQueue, int linesToRead) throws ExecutionException, InterruptedException, TimeoutException {
        this.path = path;
        this.rawTextQueue = rawTextQueue;
        this.linesToRead = linesToRead;

        buffer = ByteBuffer.allocate(1024);
        carryForward = new StringBuilder();

        prepare();
    }

    /**
     * Prepare the file reader
     */
    private void prepare(){
        fileReadingFuture = CompletableFuture.runAsync(() -> {
            doRead();
        });
    }

    /**
     * Read the file chunk by chunk
     */
    private void doRead() {
        try (AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path)) {
            System.out.println("=> File reading begins");
            List<String> batch = new ArrayList<>();
            while (true) {
                Future<Integer> future = fileChannel.read(buffer, position);
                int bytesRead = future.get(10, TimeUnit.SECONDS); // Timeout after 10 seconds

                if (bytesRead <= 0) {
                    break; // End of file
                }

                buffer.flip();

                batch = readLines(buffer, carryForward, batch);
                //Check and queue
                checkAndQueue(batch);

                buffer.compact();
                position += bytesRead;
            }

            //Add remaining lines to queue
            if(batch.size() > 0){
                RawText text = prep(batch);
                rawTextQueue.add(text);
                System.out.println("=> Reader-> Batch size: "+batch.size()+"; start line: "+text.getStarLine()+"; end line: "+text.getEndLine());
            }
            System.out.println("=> File reading completed");

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkAndQueue(List<String> batch){
        if(batch.size() >= linesToRead) {
            List<String> limitsBatch = batch.stream()
                    .limit(linesToRead)
                    .collect(Collectors.toList());

            RawText text = prep(limitsBatch);
            rawTextQueue.add(text);

            batch.subList(0, linesToRead).clear();

            System.out.println("=> Reader-> Batch size: "+limitsBatch.size()+"; start line: "+text.getStarLine()+"; end line: "+text.getEndLine());
        }
    }

    /**
     * Convert data chunks to lines
     * @param buffer
     * @param carryForward
     * @param batch
     * @return
     */
    private List<String> readLines(ByteBuffer buffer, StringBuilder carryForward, List<String> batch) {
        StringBuilder lineBuilder = new StringBuilder();

        while (buffer.hasRemaining()) {
            char c = (char) buffer.get();
            if (c == '\n') {

                if(carryForward.length() > 0){
                    String line = lineBuilder.toString();
                    line = carryForward + line;
                    batch.add(line);

                    carryForward.setLength(0);
                } else {
                    batch.add(lineBuilder.toString());
                }
                lineBuilder.setLength(0);

            } else {
                lineBuilder.append(c);
            }
        }

        if (lineBuilder.length() > 0) {
            carryForward.append(lineBuilder);
        }

        return batch;
    }

    /**
     * Prepare date for queue
     * @param lines
     * @return
     */
    private RawText prep(List<String> lines){
        endLineMarker += lines.size();
        RawText text = new RawText(lines, startLineMarker, endLineMarker);
        startLineMarker = endLineMarker+1;
        return text;
    }

    public CompletableFuture<Void> getFileReadingFuture() {
        return fileReadingFuture;
    }
}
