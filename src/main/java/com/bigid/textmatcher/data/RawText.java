package com.bigid.textmatcher.data;

import java.util.List;

/**
 * Author: Sheik Syed Ali
 */
public class RawText {
    private List<String> texts;
    private int starLine;
    private int endLine;

    public RawText(List<String> texts, int starLine, int endLine){
        this.texts = texts;
        this.starLine = starLine;
        this.endLine = endLine;
    }

    public List<String> getTexts() {
        return texts;
    }

    public int getStarLine() {
        return starLine;
    }

    public int getEndLine() {
        return endLine;
    }

    @Override
    public String toString() {
        return "BidText{" +
                "texts=" + texts +
                ", startLine=" + starLine +
                ", endLine" + endLine +
                '}';
    }
}
