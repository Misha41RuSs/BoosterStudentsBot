package com.tg.boosterbot.repository;

import com.tg.boosterbot.entity.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Integer> {
    Optional<UserPreference> findByUserIdAndPhraseHash(Integer userId, String phraseHash);
    List<UserPreference> findByUserId(Integer userId);
}
