package com.tg.boosterbot.service.pipeline.analyzers;

import com.tg.boosterbot.model.ProcessContext;
import com.tg.boosterbot.service.pipeline.PipelineStep;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@Order(1)
public class TimeOfDayAnalyzer implements PipelineStep {

    @Override
    public void execute(ProcessContext context) {
        int hour = LocalTime.now().getHour();
        if (hour >= 5 && hour < 11) {
            context.getTags().add("morning");
        } else if (hour >= 11 && hour < 18) {
            context.getTags().add("day");
        } else if (hour >= 18 && hour < 23) {
            context.getTags().add("evening");
        } else {
            context.getTags().add("night");
        }
    }
}
