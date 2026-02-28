package com.tg.boosterbot.config;

import com.tg.boosterbot.controller.BoosterBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@Component
public class BotInitializer {
    private final BoosterBot bot;

    public BotInitializer(BoosterBot bot) {
        this.bot = bot;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
            log.info("BoosterBot успешно запущен и зарегистрирован в Telegram API.");
        } catch (Exception e) {
            log.error("Критическая ошибка при инициализации бота: {}", e.getMessage());
        }
    }
}