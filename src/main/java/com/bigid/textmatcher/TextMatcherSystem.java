package com.bigid.textmatcher;

import com.bigid.textmatcher.core.BidTextMatcher;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Author: Sheik Syed Ali
 */
public class TextMatcherSystem {
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException, IOException {
        String filePath = "big.txt";
        Path path = Paths.get(filePath);
        if(!Files.exists(path)) {
            String url = "https://norvig.com/big.txt";
            System.out.println("big.txt File not found in relative path");
            System.out.println("Downloading...<"+url+">");
            downloadFile(url);
            System.out.println("Download completed");
        }

        String searchKeywords = "James,John,Robert,Michael,William,David,Richard,Charles,Joseph,Thomas,Christopher,Daniel,Paul,Mark,Donald," +
                "George,Kenneth,Steven,Edward,Brian,Ronald,Anthony,Kevin,Jason,Matthew,Gary,Timothy,Jose,Larry,Jeffrey," +
                "Frank,Scott,Eric,Stephen,Andrew,Raymond,Gregory,Joshua,Jerry,Dennis,Walter,Patrick,Peter,Harold,Douglas,Henry,Carl,Arthur,Ryan,Roger";
        int linesToRead = 10000;
        int matcherWorkers = 10;
        boolean caseSensitive = true;

        if(args.length == 4){
            searchKeywords = args[0];
            linesToRead = Integer.parseInt(args[1]);
            matcherWorkers = Integer.parseInt(args[2]);
            caseSensitive = Boolean.parseBoolean(args[3]);
        }
        System.out.println("Configuration: ");
        System.out.println("Search keywords: "+searchKeywords);
        System.out.println("Lines to read: "+linesToRead);
        System.out.println("Matcher workers: "+matcherWorkers);
        System.out.println("Is case sensitive: "+caseSensitive);
        System.out.println("=============================================");


        BidTextMatcher bidTextMatcher = new BidTextMatcher(filePath, searchKeywords)
                .matcherWorkers(matcherWorkers)
                .linesToRead(linesToRead)
                .caseSensitive(caseSensitive)
                .build();

        bidTextMatcher.start();
    }

    public static void downloadFile(String fileUrl) throws IOException {
        URL url = new URL(fileUrl);
        Path savePath = Paths.get("big.txt");

        try (InputStream in = url.openStream();
             BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(savePath))) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}
