package com.bigid.textmatcher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Sheik Syed Ali
 */
public class Checker {
    public static void main(String[] args) {
        String filePath = "dummy.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            // Read each line and append it to the StringBuilder along with a line break
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append(System.lineSeparator());
            }

            // Convert the StringBuilder to a string
            String fileContent = stringBuilder.toString();
//            System.out.println(fileContent);

//            fileContent = "he Project Gutenberg EBook of The Adventures of Sherlock Holmes\n" +
//                    "by Sir Arthur Conan Doyle\n" +
//                    "(#15 in our series by Sir Arthur Conan Doyle)\n" +
//                    "\n" +
//                    "Copyright laws are changing all over the world. Be sure to check the\n" +
//                    "copyright laws for your country before downloading or redistributing\n" +
//                    "this or any other Project Gutenberg eBook.\n" +
//                    "\n" +
//                    "This header should be the first thing seen when viewing this Project\n" +
//                    "Gutenberg file.  Please do not remove it.  Do not change or edit the\n" +
//                    "header without written permission.\n" +
//                    "\n" +
//                    "Please read the \"legal small print,\" and other information about the\n" +
//                    "eBook and Project Gutenberg at the bottom of this file.  Included is\n" +
//                    "important information about your specific rights and restrictions in\n" +
//                    "how the file may be used.  You can also find out about how to make a\n" +
//                    "donation to Project Gutenberg, and how to get involved.\n" +
//                    "\n" +
//                    "\n" +
//                    "**Welcome To The World of Free Plain Vanilla Electronic Texts**";
            System.out.println("length: "+fileContent.length());



            Pattern searchPattern = Pattern.compile("Arthur");
            Matcher matcher = searchPattern.matcher(fileContent);
            while (matcher.find()) {
                System.out.println(matcher.start());
            }

            int idx = fileContent.indexOf("Arthur");
            while (idx != -1){
                System.out.println("index: "+idx);

                idx = fileContent.indexOf("Arthur", idx+1);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
