package com.tg.boosterbot.service;

import com.tg.boosterbot.model.BoostResult;
import com.tg.boosterbot.model.ProcessContext;
import com.tg.boosterbot.service.filters.Filter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Pipeline {
    private final List<Filter> filters;

    public Pipeline(List<Filter> filters) {
        this.filters = filters;
    }

    public BoostResult getBoost(Long chatId, String message, String name) {
        ProcessContext context = ProcessContext.builder()
                .chatId(chatId)
                .userName(name)
                .userMessage(message)
                .build();

        filters.forEach(f -> f.execute(context));

        return new BoostResult(context.getResultPhrase(), context.getPhraseHash(), context.getTags());
    }
}