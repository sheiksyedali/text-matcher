package com.bigid.textmatcher;

import com.bigid.textmatcher.core.BidTextMatcher;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Author: Sheik Syed Ali
 */
public class TextMatcherSystem {
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        String filePath = "big.txt";
        if(args != null && args.length > 0){
             filePath = args[0];
        } else {
            System.out.println("File path parameter not found, So looking for big.txt file in relative path");
        }

        int linesToRead = 1000;
        String searchKeywords = "James,John,Robert,Michael,William,David,Richard,Charles,Joseph,Thomas,Christopher,Daniel,Paul,Mark,Donald," +
                "George,Kenneth,Steven,Edward,Brian,Ronald,Anthony,Kevin,Jason,Matthew,Gary,Timothy,Jose,Larry,Jeffrey," +
                "Frank,Scott,Eric,Stephen,Andrew,Raymond,Gregory,Joshua,Jerry,Dennis,Walter,Patrick,Peter,Harold,Douglas,Henry,Carl,Arthur,Ryan,Roger";

        BidTextMatcher bidTextMatcher = new BidTextMatcher(filePath, searchKeywords)
                .matcherWorkers(5)
                .linesToRead(linesToRead)
                .caseSensitive(false)
                .build();

        bidTextMatcher.start();
    }
}
