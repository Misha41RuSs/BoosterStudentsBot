package com.tg.boosterbot.repository;

import com.tg.boosterbot.entity.UserStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserStatsRepository extends JpaRepository<UserStats, Integer> {
    Optional<UserStats> findByUserId(Integer userId);
}
