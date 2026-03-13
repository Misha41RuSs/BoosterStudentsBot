package com.tg.boosterbot.service.pipeline.analyzers;

import com.tg.boosterbot.model.ProcessContext;
import com.tg.boosterbot.service.pipeline.PipelineStep;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Component
@Order(0)
public class WeekdayAnalyzer implements PipelineStep {
    @Override
    public void execute(ProcessContext context) {
        DayOfWeek day = LocalDate.now().getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            context.getTags().add("weekend");
        }
    }
}
