package com.bigid.textmatcher;

import com.bigid.textmatcher.core.TextMatcher;

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

        String searchKeywords = "shiek,syed,ali";



        TextMatcher textMatcher = new TextMatcher(filePath, searchKeywords);
        textMatcher.start();
    }
}
