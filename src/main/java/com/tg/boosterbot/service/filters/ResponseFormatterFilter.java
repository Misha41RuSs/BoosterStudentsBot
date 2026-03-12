package com.tg.boosterbot.service.filters;

import com.tg.boosterbot.model.ProcessContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Order(6)
public class ResponseFormatterFilter implements Filter {

    private static final List<String> EMOJIS_SUCCESS = List.of("🚀", "🌟", "🔥", "💪", "🏆");
    private static final List<String> EMOJIS_SAD = List.of("🫂", "❤️", "🍪", "☕", "☀️");
    private static final List<String> EMOJIS_NEUTRAL = List.of("✨", "👋", "🙌", "💫");

    @Override
    public void execute(ProcessContext context) {
        String phrase = context.getResultPhrase();
        
        if (phrase == null || phrase.isEmpty()) {
            return;
        }

        List<String> emojiList = EMOJIS_NEUTRAL;
        
        if (context.getTags().contains("success")) {
            emojiList = EMOJIS_SUCCESS;
        } else if (context.getTags().contains("sad")) {
            emojiList = EMOJIS_SAD;
        }

        String emoji = emojiList.get(ThreadLocalRandom.current().nextInt(emojiList.size()));

        // Добавляем эмодзи в конец (или начало, по вкусу) и немного форматирования
        String formattedPhrase = String.format("%s \n\n%s", phrase, emoji);
        
        context.setResultPhrase(formattedPhrase);
    }
}
