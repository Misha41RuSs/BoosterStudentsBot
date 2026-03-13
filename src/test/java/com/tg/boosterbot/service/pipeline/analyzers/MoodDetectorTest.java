package com.tg.boosterbot.service.pipeline.analyzers;

import com.tg.boosterbot.entity.User;
import com.tg.boosterbot.model.ProcessContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class MoodDetectorTest {

    private MoodDetector moodDetector;

    @BeforeEach
    void setUp() {
        moodDetector = new MoodDetector();
    }

    private ProcessContext createContext(String message) {
        return ProcessContext.builder()
                .chatId(12345L)
                .userName("TestUser")
                .userMessage(message)
                .userEntity(new User())
                .build();
    }

    @Test
    void testSuperSuccess() {
        ProcessContext context = createContext("очень отлично сдал");
        moodDetector.execute(context);
        assertTrue(context.getTags().contains("super_success"));
        assertTrue(context.getTags().contains("success"));
    }

    @Test
    void testSuccess() {
        // "рад" (2.0), 5 words: 2 / sqrt(5) = 0.89 → success (не super_success)
        ProcessContext context = createContext("рад я сегодня пошел спать");
        moodDetector.execute(context);
        assertTrue(context.getTags().contains("success"));
        assertEquals(1, context.getTags().size());
    }

    @Test
    void testNeutral() {
        ProcessContext context = createContext("я сходил на пары и пообедал");
        moodDetector.execute(context);
        assertTrue(context.getTags().contains("neutral"));
        assertEquals(1, context.getTags().size());
    }

    @Test
    void testSad() {
        // "устал" (-1.5), 6 words: -1.5 / sqrt(6) = -0.61 → sad (не super_sad)
        ProcessContext context = createContext("сегодня я немного устал на работе");
        moodDetector.execute(context);
        assertTrue(context.getTags().contains("sad"));
        assertEquals(1, context.getTags().size());
    }

    @Test
    void testSuperSad() {
        ProcessContext context = createContext("очень тяжело провал ненавижу");
        moodDetector.execute(context);
        assertTrue(context.getTags().contains("super_sad"));
        assertTrue(context.getTags().contains("sad"));
    }
}
