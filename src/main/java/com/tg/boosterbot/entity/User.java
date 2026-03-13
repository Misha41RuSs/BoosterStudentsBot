package com.tg.boosterbot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "chat_id", unique = true, nullable = false)
    private Long chatId;

    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "registered_at", insertable = false, updatable = false)
    private LocalDateTime registeredAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserStats stats;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<History> historyList = new ArrayList<>();

    public User() {
    }

    public User(Integer id, Long chatId, String username, String firstName, String lastName, LocalDateTime registeredAt, UserStats stats, List<History> historyList) {
        this.id = id;
        this.chatId = chatId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.registeredAt = registeredAt;
        this.stats = stats;
        this.historyList = historyList != null ? historyList : new ArrayList<>();
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public Integer getId() {
        return this.id;
    }

    public Long getChatId() {
        return this.chatId;
    }

    public String getUsername() {
        return this.username;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public LocalDateTime getRegisteredAt() {
        return this.registeredAt;
    }

    public UserStats getStats() {
        return this.stats;
    }

    public List<History> getHistoryList() {
        return this.historyList;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public void setStats(UserStats stats) {
        this.stats = stats;
    }

    public void setHistoryList(List<History> historyList) {
        this.historyList = historyList;
    }

    public static class UserBuilder {
        private Integer id;
        private Long chatId;
        private String username;
        private String firstName;
        private String lastName;
        private LocalDateTime registeredAt;
        private UserStats stats;
        private List<History> historyList;

        UserBuilder() {
        }

        public UserBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public UserBuilder chatId(Long chatId) {
            this.chatId = chatId;
            return this;
        }

        public UserBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserBuilder registeredAt(LocalDateTime registeredAt) {
            this.registeredAt = registeredAt;
            return this;
        }

        public UserBuilder stats(UserStats stats) {
            this.stats = stats;
            return this;
        }

        public UserBuilder historyList(List<History> historyList) {
            this.historyList = historyList;
            return this;
        }

        public User build() {
            return new User(this.id, this.chatId, this.username, this.firstName, this.lastName, this.registeredAt, this.stats, this.historyList);
        }

        public String toString() {
            return "User.UserBuilder(id=" + this.id + ", chatId=" + this.chatId + ", username=" + this.username + ", firstName=" + this.firstName + ", lastName=" + this.lastName + ", registeredAt=" + this.registeredAt + ", stats=" + this.stats + ", historyList=" + this.historyList + ")";
        }
    }
}
