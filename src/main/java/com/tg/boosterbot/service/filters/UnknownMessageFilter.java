package com.tg.boosterbot.service.filters;

import com.tg.boosterbot.model.ProcessContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(3) // Выполняется ПОСЛЕ детекторов настроения, но ДО сервиса комплиментов
public class UnknownMessageFilter implements Filter {

    @Override
    public void execute(ProcessContext context) {
        String msg = context.getUserMessage().trim();

        // Команды (начинаются с /) не считаются неизвестным текстом
        if (msg.startsWith("/")) {
            return;
        }

        List<String> tags = context.getTags();

        // Проверяем, есть ли среди тегов хоть один "эмоциональный"
        boolean hasEmotionalContext = tags.contains("sad") || tags.contains("success");

        // Если эмоционального контекста нет, значит пользователь написал что-то несвязное
        if (!hasEmotionalContext) {
            tags.add("unknown");
        }
    }
}