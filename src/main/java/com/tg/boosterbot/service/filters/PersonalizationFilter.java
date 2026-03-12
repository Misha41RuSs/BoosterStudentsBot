package com.tg.boosterbot.service.filters;

import com.tg.boosterbot.model.ProcessContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(5)
public class PersonalizationFilter implements Filter {

    @Override
    public void execute(ProcessContext context) {
        String template = context.getTemplatePhrase();
        if (template != null && context.getUserEntity() != null) {
            
            // Если шаблон ожидает форматирования %s
            if (template.contains("%s")) {
                context.setResultPhrase(String.format(template, context.getUserEntity().getFirstName()));
            } else {
                context.setResultPhrase(template);
            }
        }
    }
}
