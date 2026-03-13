package com.tg.boosterbot.controller;

import com.tg.boosterbot.service.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import com.tg.boosterbot.model.BoostResult;
import com.tg.boosterbot.repository.UserPreferenceRepository;
import com.tg.boosterbot.repository.UserRepository;
import com.tg.boosterbot.entity.User;
import com.tg.boosterbot.entity.UserPreference;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class BoosterBot extends TelegramLongPollingBot {

    private static final Logger log = LoggerFactory.getLogger(BoosterBot.class);

    private static final List<String> EMOJIS_SUCCESS = List.of("🚀", "🌟", "🔥", "💪", "🏆");
    private static final List<String> EMOJIS_SUPER_SUCCESS = List.of("🥇", "🎉", "✨", "👑", "😍");
    private static final List<String> EMOJIS_SAD = List.of("🫂", "❤️", "🌙", "🩹", "🍊");
    private static final List<String> EMOJIS_SUPER_SAD = List.of("💔", "🌧️", "🩹", "❤️‍🩹", "🌅");
    private static final List<String> EMOJIS_NEUTRAL = List.of("☕", "👋", "🌿", "⚖️", "💻");
    private static final List<String> EMOJIS_UNKNOWN = List.of("🤔", "😅", "👀");

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    private final Pipeline pipeline;
    private final UserPreferenceRepository userPreferenceRepository;
    private final UserRepository userRepository;

    public BoosterBot(Pipeline pipeline, UserPreferenceRepository userPreferenceRepository, UserRepository userRepository) {
        this.pipeline = pipeline;
        this.userPreferenceRepository = userPreferenceRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
            return;
        }

        if (update.hasMessage() && update.getMessage().hasText()) {

            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String userFirstName = update.getMessage().getFrom().getFirstName();

            BoostResult boostResult = pipeline.getBoost(chatId, messageText, userFirstName);

            String formattedText = formatWithEmoji(boostResult.getText(), boostResult.getTags());
            sendAnswerWithKeyboard(chatId, formattedText, boostResult.getPhraseHash());

            log.info("User: {} | Message: {} | Response: {}",
                    userFirstName, messageText, formattedText);

        } else if (update.hasMessage()) {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(update.getMessage().getChatId()));
            message.setText("Я понимаю только текстовые сообщения! 😊");
            try {
                execute(message);
            } catch (Exception e) {
                log.error("Ошибка отправки сообщения", e);
            }
        }
    }

    private String formatWithEmoji(String text, List<String> tags) {
        List<String> emojiList = EMOJIS_NEUTRAL;
        if (tags.contains("super_success")) {
            emojiList = EMOJIS_SUPER_SUCCESS;
        } else if (tags.contains("success")) {
            emojiList = EMOJIS_SUCCESS;
        } else if (tags.contains("super_sad")) {
            emojiList = EMOJIS_SUPER_SAD;
        } else if (tags.contains("sad")) {
            emojiList = EMOJIS_SAD;
        } else if (tags.contains("unknown")) {
            emojiList = EMOJIS_UNKNOWN;
        }
        String emoji = emojiList.get(ThreadLocalRandom.current().nextInt(emojiList.size()));
        return String.format("%s \n\n%s", text, emoji);
    }

    private void sendAnswerWithKeyboard(long chatId, String text, String phraseHash) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        if (phraseHash != null && !phraseHash.isEmpty()) {
            InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
            
            InlineKeyboardButton likeButton = new InlineKeyboardButton();
            likeButton.setText("👍");
            likeButton.setCallbackData("LIKE_" + phraseHash);
            
            InlineKeyboardButton dislikeButton = new InlineKeyboardButton();
            dislikeButton.setText("👎");
            dislikeButton.setCallbackData("DISL_" + phraseHash);
            
            markupInLine.setKeyboard(List.of(List.of(likeButton, dislikeButton)));
            message.setReplyMarkup(markupInLine);
        }

        try {
            execute(message);
        } catch (Exception e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callbackQuery.getId());

        try {
            if (data.startsWith("LIKE_") || data.startsWith("DISL_")) {
                int score = data.startsWith("LIKE_") ? 1 : -1;
                String phraseHash = data.substring(5);

                Optional<User> userOpt = userRepository.findByChatId(chatId);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    Optional<UserPreference> prefOpt = userPreferenceRepository.findByUserIdAndPhraseHash(user.getId(), phraseHash);
                    UserPreference pref = prefOpt.orElse(new UserPreference(user, phraseHash, 0));
                    pref.setScore(score);
                    userPreferenceRepository.save(pref);
                }

                answer.setText("Спасибо за оценку!");

                // Remove the keyboard after voting
                EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
                editMarkup.setChatId(String.valueOf(chatId));
                editMarkup.setMessageId(messageId);
                editMarkup.setReplyMarkup(null);
                execute(editMarkup);
            }
            execute(answer);
        } catch (Exception e) {
            log.error("Ошибка обработки callback query", e);
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