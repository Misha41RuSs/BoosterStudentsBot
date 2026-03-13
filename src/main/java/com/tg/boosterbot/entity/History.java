package com.tg.boosterbot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "history")
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private String command;

    @Column(name = "message_text")
    private String messageText;

    @Column(name = "detected_mood")
    private String detectedMood;

    @Column(name = "response_type")
    private String responseType;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public History() {
    }

    public History(Integer id, User user, String command, String messageText, String detectedMood, String responseType, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.command = command;
        this.messageText = messageText;
        this.detectedMood = detectedMood;
        this.responseType = responseType;
        this.createdAt = createdAt;
    }

    public static HistoryBuilder builder() {
        return new HistoryBuilder();
    }

    public Integer getId() {
        return this.id;
    }

    public User getUser() {
        return this.user;
    }

    public String getCommand() {
        return this.command;
    }

    public String getMessageText() {
        return this.messageText;
    }

    public String getDetectedMood() {
        return this.detectedMood;
    }

    public String getResponseType() {
        return this.responseType;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public void setDetectedMood(String detectedMood) {
        this.detectedMood = detectedMood;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static class HistoryBuilder {
        private Integer id;
        private User user;
        private String command;
        private String messageText;
        private String detectedMood;
        private String responseType;
        private LocalDateTime createdAt;

        HistoryBuilder() {
        }

        public HistoryBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public HistoryBuilder user(User user) {
            this.user = user;
            return this;
        }

        public HistoryBuilder command(String command) {
            this.command = command;
            return this;
        }

        public HistoryBuilder messageText(String messageText) {
            this.messageText = messageText;
            return this;
        }

        public HistoryBuilder detectedMood(String detectedMood) {
            this.detectedMood = detectedMood;
            return this;
        }

        public HistoryBuilder responseType(String responseType) {
            this.responseType = responseType;
            return this;
        }

        public HistoryBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public History build() {
            return new History(this.id, this.user, this.command, this.messageText, this.detectedMood, this.responseType, this.createdAt);
        }

        public String toString() {
            return "History.HistoryBuilder(id=" + this.id + ", user=" + this.user + ", command=" + this.command + ", messageText=" + this.messageText + ", detectedMood=" + this.detectedMood + ", responseType=" + this.responseType + ", createdAt=" + this.createdAt + ")";
        }
    }
}
