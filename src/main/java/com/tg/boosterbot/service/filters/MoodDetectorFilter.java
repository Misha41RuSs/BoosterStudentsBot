package com.tg.boosterbot.service.filters;

import com.tg.boosterbot.model.ProcessContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Order(2)
public class MoodDetectorFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(MoodDetectorFilter.class);

    private static final Map<String, Double> WORD_WEIGHTS = Map.ofEntries(

            // позитив
            Map.entry("отлично", 3.0),
            Map.entry("супер", 2.5),
            Map.entry("ура", 2.5),
            Map.entry("успех", 3.0),
            Map.entry("сдал", 2.0),
            Map.entry("рад", 2.0),
            Map.entry("круто", 2.5),
            Map.entry("получилось", 2.0),
            Map.entry("молодец", 2.5),

            // негатив
            Map.entry("устал", -1.5),
            Map.entry("грустно", -2.5),
            Map.entry("плохо", -2.0),
            Map.entry("печально", -2.0),
            Map.entry("ненавижу", -3.0),
            Map.entry("сложно", -1.5),
            Map.entry("тяжело", -2.0),
            Map.entry("провал", -3.0)
    );


    private static final Map<String, Double> INTENSIFIERS = Map.of(
            "очень", 1.5,
            "сильно", 1.5,
            "мега", 1.7
    );

    @Override
    public void execute(ProcessContext context) {

        String message = context.getUserMessage().toLowerCase();

        log.info("=== Начинаем анализ настроения ===");
        log.info("Сообщение пользователя: {}", message);

        String[] words = message.split("\\s+");

        Map<String, Integer> tf = new HashMap<>();

        for (String word : words) {
            word = word.replaceAll("[^а-яА-Я]", "");
            if (word.isEmpty()) continue;

            tf.put(word, tf.getOrDefault(word, 0) + 1);
        }

        log.info("TF слов: {}", tf);

        double score = 0.0;

        List<String> wordLogs = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : tf.entrySet()) {

            String word = entry.getKey();
            int freq = entry.getValue();

            double weight = WORD_WEIGHTS.getOrDefault(word, 0.0);

            double contribution = weight * freq;

            score += contribution;

            wordLogs.add(word + "(" + weight + " * " + freq + ")=" + contribution);
        }

        log.info("Вклад слов в настроение: {}", wordLogs);

        // проверяем усилители
        for (Map.Entry<String, Integer> entry : tf.entrySet()) {
            String word = entry.getKey();
            int freq = entry.getValue();
            
            if (INTENSIFIERS.containsKey(word)) {
                // Применяем усилитель столько раз, сколько встретилось слово
                double multiplier = Math.pow(INTENSIFIERS.get(word), freq);

                log.info("Найден усилитель '{}' ({} раз) → множитель {}", word, freq, multiplier);

                score *= multiplier;
            }
        }

        // нормализация по количеству значимых очищенных слов, 
        // используем квадратный корень, чтобы смягчить штраф за длину текста
        int cleanWordsCount = tf.values().stream().mapToInt(Integer::intValue).sum();
        double normalizedScore = cleanWordsCount > 0 ? score / Math.max(1, Math.sqrt(cleanWordsCount)) : score;

        log.info("Raw score = {}", score);
        log.info("Normalized score = {}", normalizedScore);

        if (normalizedScore >= 0.3) {

            context.getTags().add("success");
            log.info("Определено настроение: SUCCESS");

        } else if (normalizedScore <= -0.3) {

            context.getTags().add("sad");
            log.info("Определено настроение: SAD");

        } else {

            log.info("Настроение нейтральное");
        }

        log.info("=== Конец анализа настроения ===");
    }
}