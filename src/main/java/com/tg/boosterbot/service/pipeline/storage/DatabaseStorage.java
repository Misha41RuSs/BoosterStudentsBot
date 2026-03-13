package com.tg.boosterbot.service.pipeline.storage;

import com.tg.boosterbot.entity.History;
import com.tg.boosterbot.entity.User;
import com.tg.boosterbot.entity.UserStats;
import com.tg.boosterbot.model.ProcessContext;
import com.tg.boosterbot.repository.HistoryRepository;
import com.tg.boosterbot.repository.UserStatsRepository;
import com.tg.boosterbot.service.pipeline.PipelineStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(7)
public class DatabaseStorage implements PipelineStep {

    private static final Logger log = LoggerFactory.getLogger(DatabaseStorage.class);

    private final HistoryRepository historyRepository;
    private final UserStatsRepository userStatsRepository;

    public DatabaseStorage(HistoryRepository historyRepository, UserStatsRepository userStatsRepository) {
        this.historyRepository = historyRepository;
        this.userStatsRepository = userStatsRepository;
    }

    @Override
    public void execute(ProcessContext context) {
        User user = context.getUserEntity();

        if (user == null) {
            log.warn("DatabaseStorage: пользователь не найден в контексте, сохранение пропущено");
            return;
        }

        List<String> tags = context.getTags();
        String detectedMood = "neutral";
        if (tags.contains("success")) {
            detectedMood = "success";
        } else if (tags.contains("sad")) {
            detectedMood = "sad";
        }

        History history = History.builder()
                .user(user)
                .command("message")
                .messageText(context.getUserMessage())
                .detectedMood(detectedMood)
                .responseType(context.getTemplatePhrase() != null ? "boost" : "unknown")
                .build();
        historyRepository.save(history);

        UserStats stats = user.getStats();
        if (stats != null) {
            Integer currentBoosts = stats.getBoostsReceived();
            stats.setBoostsReceived((currentBoosts == null ? 0 : currentBoosts) + 1);
            stats.setLastMood(detectedMood);
            userStatsRepository.save(stats);
        }

        log.info("Статистика и история успешно сохранены для пользователя {}", user.getFirstName());
    }
}
