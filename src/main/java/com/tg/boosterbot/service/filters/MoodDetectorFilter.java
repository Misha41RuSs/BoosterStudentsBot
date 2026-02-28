package com.tg.boosterbot.service.filters;

import com.tg.boosterbot.model.ProcessContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class MoodDetectorFilter implements Filter {
    @Override
    public void execute(ProcessContext context) {
        String msg = context.getUserMessage().toLowerCase();

        if (msg.contains("груст") || msg.contains("устал")) {
            context.getTags().add("sad");
        }
        if (msg.contains("сдал") || msg.contains("ура") || msg.contains("успех")) {
            context.getTags().add("success");
        }
    }
}