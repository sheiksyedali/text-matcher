package com.bigid.textmatcher.data;

/**
 * Author: Sheik Syed Ali
 */
public class Offset {
    private int lineOffset;
    private int charOffset;

    public Offset(int lineOffset, int charOffset){
        this.lineOffset = lineOffset;
        this.charOffset = charOffset;
    }
    public int getLineOffset() {
        return lineOffset;
    }

    public int getCharOffset() {
        return charOffset;
    }

    @Override
    public String toString() {
        return "Offset{" +
                "lineOffset=" + lineOffset +
                ", charOffset=" + charOffset +
                '}';
    }
}
