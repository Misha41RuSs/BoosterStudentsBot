package com.tg.boosterbot.controller;

import com.tg.boosterbot.service.ComplimentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

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
            String userFirstName = update.getMessage().getFrom().getFirstName();
            if ("/compliment".equals(messageText)) {
                String answer = complimentService.getRandomCompliment(userFirstName);
                sendAnswer(chatId, answer);
                System.out.println("LOG: Пользователь " + userFirstName + " получил комплимент: [" + answer + "]");

            } else if (messageText.startsWith("/")) {
                sendAnswer(chatId, "Извини, " + userFirstName + ", я пока знаю только команду /compliment");
                System.out.println("LOG: Пользователь " + userFirstName + " ввел неизвестную команду: " + messageText);

            } else {
                sendAnswer(chatId, "Пожалуйста, используй команду /compliment, чтобы я мог тебя подбодрить!");
            }
        } else if (update.hasMessage()) {
            long chatId = update.getMessage().getChatId();
            sendAnswer(chatId, "Я понимаю только текстовые команды. Попробуй /compliment");
            System.out.println("LOG: Пользователь прислал нетекстовое сообщение.");
        }
    }

    private void sendAnswer(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        try {
            execute(message);
        } catch (Exception e) {
            System.err.println("Ошибка при отправке: " + e.getMessage());
        }
    }

    @Override public String getBotUsername() { return botName; }
    @Override public String getBotToken() { return botToken; }
}