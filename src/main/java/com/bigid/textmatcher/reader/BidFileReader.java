package com.bigid.textmatcher.reader;

import com.bigid.textmatcher.data.RawText;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Author: Sheik Syed Ali
 */
public class BidFileReader {
    private final int LINES_READ = 500;
    private Path path;
    private BlockingQueue<RawText> rawTextQueue;
    private long position = 0;
    private ByteBuffer buffer;

    private StringBuilder carryForward;

    private int startLineMarker = 1;
    private int endLineMarker = 0;

    private CompletableFuture<Void> fileReadingFuture;

    public BidFileReader(Path path, BlockingQueue<RawText> rawTextQueue) throws ExecutionException, InterruptedException, TimeoutException {
        this.path = path;
        this.rawTextQueue = rawTextQueue;

        buffer = ByteBuffer.allocate(1024);
        carryForward = new StringBuilder();

        prepare();
    }

    private void prepare(){
        fileReadingFuture = CompletableFuture.runAsync(() -> {
            doRead();
        });
    }

    private void doRead() {
        try (AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path)) {
            while (true) {
                Future<Integer> future = fileChannel.read(buffer, position);
                int bytesRead = future.get(10, TimeUnit.SECONDS); // Timeout after 10 seconds

                if (bytesRead <= 0) {
                    System.out.println("Read completed.....");
                    break; // End of file
                }

                buffer.flip();

                List<String> lines = readLines(buffer, carryForward);

                //handle batch size like 500, 1000 here

                RawText text = prep(lines);
                rawTextQueue.add(text);

                buffer.compact();
                position += bytesRead;
            }

//            rawTextQueue.add(new BidText(new ArrayList<>(),-1, -1));//Dummy

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

    private static List<String> readLines(ByteBuffer buffer, StringBuilder carryForward) {
        List<String> lines = new ArrayList<>();
        StringBuilder lineBuilder = new StringBuilder();

        while (buffer.hasRemaining()) {
            char c = (char) buffer.get();
            if (c == '\n') {

                if(carryForward.length() > 0){
                    String line = lineBuilder.toString();
                    line = carryForward + line;

                    lines.add(line);

//                    System.out.println("<<<<"+carryForward+">>>>>");
//                    System.out.println("<Partial appended> Read line: "+line);

                    carryForward.setLength(0);
                } else {
                    lines.add(lineBuilder.toString());

//                    System.out.println("Read line: "+lineBuilder.toString());
                }
                lineBuilder.setLength(0);

            } else {
                lineBuilder.append(c);
            }
        }

        if (lineBuilder.length() > 0) {
            carryForward.append(lineBuilder);
        }

        return lines;
    }

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
