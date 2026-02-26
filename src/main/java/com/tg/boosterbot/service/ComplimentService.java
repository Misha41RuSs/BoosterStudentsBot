package com.tg.boosterbot.service;

import com.tg.boosterbot.model.ComplimentData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.Random;

@Service
public class ComplimentService {
    private ComplimentData data;
    private final Random random = new Random();

    @PostConstruct // Загрузится один раз при старте
    public void init() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        data = mapper.readValue(new ClassPathResource("compliments.json").getFile(), ComplimentData.class);
    }

    public String getRandomCompliment(String name) {
        String phrase = data.getPhrases().get(random.nextInt(data.getPhrases().size()));
        return String.format(phrase, name);
    }
}