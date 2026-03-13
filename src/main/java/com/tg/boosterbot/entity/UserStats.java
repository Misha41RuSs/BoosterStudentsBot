package com.tg.boosterbot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_stats")
public class UserStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "last_mood")
    private String lastMood;

    @Column(name = "boosts_received", columnDefinition = "integer default 0")
    private Integer boostsReceived = 0;

    @Column(name = "compliments_received", columnDefinition = "integer default 0")
    private Integer complimentsReceived = 0;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public UserStats() {
    }

    public UserStats(Integer id, User user, String lastMood, Integer boostsReceived, Integer complimentsReceived, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.lastMood = lastMood;
        this.boostsReceived = boostsReceived != null ? boostsReceived : 0;
        this.complimentsReceived = complimentsReceived != null ? complimentsReceived : 0;
        this.updatedAt = updatedAt;
    }

    public static UserStatsBuilder builder() {
        return new UserStatsBuilder();
    }

    public Integer getId() {
        return this.id;
    }

    public User getUser() {
        return this.user;
    }

    public String getLastMood() {
        return this.lastMood;
    }

    public Integer getBoostsReceived() {
        return this.boostsReceived;
    }

    public Integer getComplimentsReceived() {
        return this.complimentsReceived;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setLastMood(String lastMood) {
        this.lastMood = lastMood;
    }

    public void setBoostsReceived(Integer boostsReceived) {
        this.boostsReceived = boostsReceived;
    }

    public void setComplimentsReceived(Integer complimentsReceived) {
        this.complimentsReceived = complimentsReceived;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static class UserStatsBuilder {
        private Integer id;
        private User user;
        private String lastMood;
        private Integer boostsReceived;
        private Integer complimentsReceived;
        private LocalDateTime updatedAt;

        UserStatsBuilder() {
        }

        public UserStatsBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public UserStatsBuilder user(User user) {
            this.user = user;
            return this;
        }

        public UserStatsBuilder lastMood(String lastMood) {
            this.lastMood = lastMood;
            return this;
        }

        public UserStatsBuilder boostsReceived(Integer boostsReceived) {
            this.boostsReceived = boostsReceived;
            return this;
        }

        public UserStatsBuilder complimentsReceived(Integer complimentsReceived) {
            this.complimentsReceived = complimentsReceived;
            return this;
        }

        public UserStatsBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public UserStats build() {
            return new UserStats(this.id, this.user, this.lastMood, this.boostsReceived, this.complimentsReceived, this.updatedAt);
        }

        public String toString() {
            return "UserStats.UserStatsBuilder(id=" + this.id + ", user=" + this.user + ", lastMood=" + this.lastMood + ", boostsReceived=" + this.boostsReceived + ", complimentsReceived=" + this.complimentsReceived + ", updatedAt=" + this.updatedAt + ")";
        }
    }
}
