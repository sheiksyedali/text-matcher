package com.bigid.textmatcher.data;

import java.util.List;

/**
 * Author: Sheik Syed Ali
 */
public class RawText {
    private List<String> texts;
    private int startIndex;
    private int endIndex;

    public RawText(List<String> texts, int startIndex, int endIndex){
        this.texts = texts;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public List<String> getTexts() {
        return texts;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    @Override
    public String toString() {
        return "BidText{" +
                "texts=" + texts +
                ", startIndex=" + startIndex +
                ", endIndex=" + endIndex +
                '}';
    }
}
