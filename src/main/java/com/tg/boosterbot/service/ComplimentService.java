package com.tg.boosterbot.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tg.boosterbot.model.ProcessContext;
import com.tg.boosterbot.service.filters.Filter;
import jakarta.annotation.PostConstruct;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Order(4)
public class ComplimentService implements Filter {

    private Map<String, List<String>> compliments;

    @PostConstruct
    public void init() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getResourceAsStream("/compliments.json");
        compliments = mapper.readValue(is, new TypeReference<Map<String, List<String>>>() {});
    }

    @Override
    public void execute(ProcessContext context) {
        List<String> tags = context.getTags();

        if (tags.contains("unknown")) {
            setResponse(context, "unknown");
            return;
        }

        Set<String> contextTags = new HashSet<>(tags);
        String bestKey = "default";
        int maxMatches = 0;

        for (String key : compliments.keySet()) {
            if (key.equals("default") || key.equals("unknown")) continue;

            Set<String> keyTags = new HashSet<>(Arrays.asList(key.split("_")));

            if (contextTags.containsAll(keyTags)) {
                if (keyTags.size() > maxMatches) {
                    maxMatches = keyTags.size();
                    bestKey = key;
                }
            }
        }

        setResponse(context, bestKey);
    }

    private void setResponse(ProcessContext context, String key) {
        List<String> phrases = compliments.getOrDefault(key, compliments.get("default"));
        String phrase = phrases.get(ThreadLocalRandom.current().nextInt(phrases.size()));
        context.setResultPhrase(String.format(phrase, context.getUserName()));
    }
}