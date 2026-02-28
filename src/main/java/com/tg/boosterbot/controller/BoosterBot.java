package com.tg.boosterbot.controller;

import com.tg.boosterbot.service.ComplimentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class BoosterBot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    private final ComplimentService complimentService;

    public BoosterBot(ComplimentService complimentService) {
        this.complimentService = complimentService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getFrom().getFirstName();

            if ("/compliment".equals(messageText)) {
                String answer = complimentService.getRandomCompliment(userName);
                sendAnswer(chatId, answer);
                log.info("Успех: Отправлен комплимент пользователю {} (ID: {})", userName, chatId);

            } else if (messageText.startsWith("/")) {
                sendAnswer(chatId, "Извини, " + userName + ", я пока знаю только команду /compliment");
                log.warn("Неизвестная команда: {} от пользователя {}", messageText, userName);

            } else {
                sendAnswer(chatId, "Пожалуйста, используй команду /compliment, чтобы я мог тебя подбодрить!");
            }
        } else if (update.hasMessage()) {
            log.info("Получено нетекстовое сообщение от пользователя {}", update.getMessage().getFrom().getFirstName());
            sendAnswer(update.getMessage().getChatId(), "Я понимаю только текстовые команды. Попробуй /compliment");
        }
    }

    private void sendAnswer(long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(text)
                .build();
        try {
            execute(message);
        } catch (Exception e) {
            log.error("Ошибка при отправке сообщения в чат {}: {}", chatId, e.getMessage());
        }
    }

    @Override public String getBotUsername() { return botName; }
    @Override public String getBotToken() { return botToken; }
}