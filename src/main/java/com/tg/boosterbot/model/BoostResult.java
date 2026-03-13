package com.tg.boosterbot.model;

import java.util.List;

public class BoostResult {
    private final String text;
    private final String phraseHash;
    private final List<String> tags;

    public BoostResult(String text, String phraseHash, List<String> tags) {
        this.text = text;
        this.phraseHash = phraseHash;
        this.tags = tags != null ? tags : List.of();
    }

    public String getText() {
        return text;
    }

    public String getPhraseHash() {
        return phraseHash;
    }

    public List<String> getTags() {
        return tags;
    }
}
