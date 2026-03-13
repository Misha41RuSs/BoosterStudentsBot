package com.tg.boosterbot.service.filters;

import com.tg.boosterbot.entity.User;
import com.tg.boosterbot.model.ProcessContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MoodDetectorFilterTest {

    private MoodDetectorFilter moodDetectorFilter;

    @BeforeEach
    void setUp() {
        moodDetectorFilter = new MoodDetectorFilter();
    }

    private ProcessContext createContext(String message) {
        ProcessContext context = ProcessContext.builder()
                .chatId(12345L)
                .userName("TestUser")
                .userMessage(message)
                .userEntity(new User())
                .build();
        return context;
    }

    @Test
    void testSuperSuccess() {
        ProcessContext context = createContext("очень отлично сдал");
        moodDetectorFilter.execute(context);
        
        assertTrue(context.getTags().contains("super_success"));
        assertTrue(context.getTags().contains("success"));
    }

    @Test
    void testSuccess() {
        // Needs to have a score between 0.3 and 1.0
        // "рад" (2.0), total 4 words. 2 / sqrt(4) = 1.0 -> super_success
        // Let's use 5 words: "рад я сегодня пошел спать" -> 2 / sqrt(5) = 0.89 -> success
        ProcessContext context = createContext("рад я сегодня пошел спать");
        moodDetectorFilter.execute(context);

        assertTrue(context.getTags().contains("success"));
        assertEquals(1, context.getTags().size()); // Only "success", not "super_success"
    }

    @Test
    void testNeutral() {
        ProcessContext context = createContext("я сходил на пары и пообедал");
        moodDetectorFilter.execute(context);

        assertTrue(context.getTags().contains("neutral"));
        assertEquals(1, context.getTags().size());
    }

    @Test
    void testSad() {
        // Needs a score between -1.0 and -0.3
        // "устал" (-1.5), 6 words. -1.5 / sqrt(6) = -0.61 -> sad
        ProcessContext context = createContext("сегодня я немного устал на работе");
        moodDetectorFilter.execute(context);

        assertTrue(context.getTags().contains("sad"));
        assertEquals(1, context.getTags().size()); // Only "sad", not "super_sad"
    }

    @Test
    void testSuperSad() {
        ProcessContext context = createContext("очень тяжело провал ненавижу");
        moodDetectorFilter.execute(context);

        assertTrue(context.getTags().contains("super_sad"));
        assertTrue(context.getTags().contains("sad"));
    }
}
