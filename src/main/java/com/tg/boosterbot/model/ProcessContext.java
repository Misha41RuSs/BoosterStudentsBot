package com.tg.boosterbot.model;

import com.tg.boosterbot.entity.User;

import java.util.ArrayList;
import java.util.List;

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
    
    // Уникальный хеш-идентификатор выбранной фразы для системы лайков
    private String phraseHash;

    ProcessContext(Long chatId, String userName, String userMessage, User userEntity, String templatePhrase, String resultPhrase, String phraseHash) {
        this.chatId = chatId;
        this.userName = userName;
        this.userMessage = userMessage;
        this.userEntity = userEntity;
        this.templatePhrase = templatePhrase;
        this.resultPhrase = resultPhrase;
        this.phraseHash = phraseHash;
    }

    public static ProcessContextBuilder builder() {
        return new ProcessContextBuilder();
    }

    public Long getChatId() {
        return this.chatId;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getUserMessage() {
        return this.userMessage;
    }

    public User getUserEntity() {
        return this.userEntity;
    }

    public List<String> getTags() {
        return this.tags;
    }

    public String getTemplatePhrase() {
        return this.templatePhrase;
    }

    public String getResultPhrase() {
        return this.resultPhrase;
    }

    public String getPhraseHash() {
        return this.phraseHash;
    }

    public void setUserEntity(User userEntity) {
        this.userEntity = userEntity;
    }

    public void setTemplatePhrase(String templatePhrase) {
        this.templatePhrase = templatePhrase;
    }

    public void setResultPhrase(String resultPhrase) {
        this.resultPhrase = resultPhrase;
    }

    public void setPhraseHash(String phraseHash) {
        this.phraseHash = phraseHash;
    }

    public static class ProcessContextBuilder {
        private Long chatId;
        private String userName;
        private String userMessage;
        private User userEntity;
        private String templatePhrase;
        private String resultPhrase;
        private String phraseHash;

        ProcessContextBuilder() {
        }

        public ProcessContextBuilder chatId(Long chatId) {
            this.chatId = chatId;
            return this;
        }

        public ProcessContextBuilder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public ProcessContextBuilder userMessage(String userMessage) {
            this.userMessage = userMessage;
            return this;
        }

        public ProcessContextBuilder userEntity(User userEntity) {
            this.userEntity = userEntity;
            return this;
        }

        public ProcessContextBuilder templatePhrase(String templatePhrase) {
            this.templatePhrase = templatePhrase;
            return this;
        }

        public ProcessContextBuilder resultPhrase(String resultPhrase) {
            this.resultPhrase = resultPhrase;
            return this;
        }

        public ProcessContextBuilder phraseHash(String phraseHash) {
            this.phraseHash = phraseHash;
            return this;
        }

        public ProcessContext build() {
            return new ProcessContext(this.chatId, this.userName, this.userMessage, this.userEntity, this.templatePhrase, this.resultPhrase, this.phraseHash);
        }

        public String toString() {
            return "ProcessContext.ProcessContextBuilder(chatId=" + this.chatId + ", userName=" + this.userName + ", userMessage=" + this.userMessage + ", userEntity=" + this.userEntity + ", templatePhrase=" + this.templatePhrase + ", resultPhrase=" + this.resultPhrase + ")";
        }
    }
}