package com.tg.boosterbot.controller;

import com.tg.boosterbot.service.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class BoosterBot extends TelegramLongPollingBot {

    private static final Logger log = LoggerFactory.getLogger(BoosterBot.class);

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    private final Pipeline pipeline;

    public BoosterBot(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {

            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String userFirstName = update.getMessage().getFrom().getFirstName();

            String answer = pipeline.getBoost(chatId, messageText, userFirstName);

            sendAnswer(chatId, answer);

            log.info("User: {} | Message: {} | Response: {}",
                    userFirstName, messageText, answer);

        } else if (update.hasMessage()) {
            sendAnswer(update.getMessage().getChatId(),
                    "Я понимаю только текстовые сообщения! 😊");
        }
    }

    private void sendAnswer(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            execute(message);
        } catch (Exception e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}