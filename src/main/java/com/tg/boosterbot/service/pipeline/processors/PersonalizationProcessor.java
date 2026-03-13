package com.tg.boosterbot.service.pipeline.processors;

import com.tg.boosterbot.model.ProcessContext;
import com.tg.boosterbot.service.pipeline.PipelineStep;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(5)
public class PersonalizationProcessor implements PipelineStep {

    @Override
    public void execute(ProcessContext context) {
        String template = context.getTemplatePhrase();
        if (template != null && context.getUserEntity() != null) {
            if (template.contains("%s")) {
                context.setResultPhrase(String.format(template, context.getUserEntity().getFirstName()));
            } else {
                context.setResultPhrase(template);
            }
        }
    }
}
