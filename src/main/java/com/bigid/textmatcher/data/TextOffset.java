package com.bigid.textmatcher.data;

import com.bigid.textmatcher.data.Offset;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Sheik Syed Ali
 */
public class TextOffset {
    private String text;

    private List<Offset> offsets;

    public TextOffset(String text){
        this.text = text;
        offsets = new ArrayList<>();
    }

    public void addOffset(Offset offset){
        offsets.add(offset);
    }

    public String getText() {
        return text;
    }

    public List<Offset> getOffsets() {
        return offsets;
    }

    public void setOffsets(List<Offset> offsets) {
        this.offsets = offsets;
    }

    @Override
    public String toString() {
        return "TextOffset{" +
                "text='" + text + '\'' +
                ", offsets=" + offsets +
                '}';
    }
}
