package com.tg.boosterbot.model;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ProcessContext {
    private final String userName;
    private final String userMessage;

    private final List<String> tags = new ArrayList<>();
    private String resultPhrase;
}