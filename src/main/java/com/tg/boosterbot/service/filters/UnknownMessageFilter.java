package com.tg.boosterbot.service.filters;

import com.tg.boosterbot.model.ProcessContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(3)
public class UnknownMessageFilter implements Filter {

    @Override
    public void execute(ProcessContext context) {
        String msg = context.getUserMessage().trim();

        if (msg.startsWith("/")) {
            return;
        }

        List<String> tags = context.getTags();

        boolean hasEmotionalContext = tags.contains("success") || 
                                      tags.contains("super_success") || 
                                      tags.contains("sad") || 
                                      tags.contains("super_sad") || 
                                      tags.contains("neutral");

        if (!hasEmotionalContext) {
            tags.add("unknown");
        }
    }
}