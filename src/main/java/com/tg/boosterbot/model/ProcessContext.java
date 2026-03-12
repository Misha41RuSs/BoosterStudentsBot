package com.tg.boosterbot.model;

import com.tg.boosterbot.entity.User;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ProcessContext {
    private final Long chatId;
    private final String userName;
    private final String userMessage;

    private User userEntity;
    private final List<String> tags = new ArrayList<>();
    
    // Шаблон ответа (без подставленного имени), устанавливается в ComplimentGeneratorFilter
    private String templatePhrase;
    
    // Итоговый ответ для отправки пользователю
    private String resultPhrase;
}