package com.tg.boosterbot.service.filters;

import com.tg.boosterbot.model.ProcessContext;

public interface Filter {
    void execute(ProcessContext context);
}