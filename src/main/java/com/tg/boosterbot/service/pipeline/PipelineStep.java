package com.tg.boosterbot.service.pipeline;

import com.tg.boosterbot.model.ProcessContext;

public interface PipelineStep {
    void execute(ProcessContext context);
}
