package com.tg.boosterbot.service.filters;

import com.tg.boosterbot.entity.History;
import com.tg.boosterbot.entity.User;
import com.tg.boosterbot.entity.UserStats;
import com.tg.boosterbot.model.ProcessContext;
import com.tg.boosterbot.repository.HistoryRepository;
import com.tg.boosterbot.repository.UserStatsRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(7)
@RequiredArgsConstructor
public class DatabaseStorageFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(DatabaseStorageFilter.class);

    private final HistoryRepository historyRepository;
    private final UserStatsRepository userStatsRepository;

    @Override
    public void execute(ProcessContext context) {
        User user = context.getUserEntity();

        if (user == null) {
            log.warn("DatabaseStorageFilter: Пользователь не найден в контексте, пропускаем сохранение");
            return;
        }

        List<String> tags = context.getTags();
        String detectedMood = "neutral";
        
        if (tags.contains("success")) {
            detectedMood = "success";
        } else if (tags.contains("sad")) {
            detectedMood = "sad";
        }

        // 1. Сохраняем историю
        History history = History.builder()
                .user(user)
                .command("message") // или получать команду, если была
                .messageText(context.getUserMessage())
                .detectedMood(detectedMood)
                .responseType(context.getTemplatePhrase() != null ? "boost" : "unknown")
                .build();
        
        historyRepository.save(history);

        // 2. Обновляем статистику пользователя
        UserStats stats = user.getStats();
        if (stats != null) {
            stats.setBoostsReceived(stats.getBoostsReceived() + 1);
            stats.setLastMood(detectedMood);
            // Если в будущем вы будете считать отдельно `complimentsReceived`, можно добавить логику
            userStatsRepository.save(stats);
        }
        
        log.info("Статистика и история успешно сохранены для пользователя {}", user.getFirstName());
    }
}
