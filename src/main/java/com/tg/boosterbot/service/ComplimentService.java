package com.tg.boosterbot.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tg.boosterbot.entity.User;
import com.tg.boosterbot.entity.UserPreference;
import com.tg.boosterbot.repository.UserPreferenceRepository;
import com.tg.boosterbot.model.ProcessContext;
import com.tg.boosterbot.service.filters.Filter;
import jakarta.annotation.PostConstruct;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Order(4)
public class ComplimentService implements Filter {

    private Map<String, List<String>> compliments;
    private final UserPreferenceRepository userPreferenceRepository;

    public ComplimentService(UserPreferenceRepository userPreferenceRepository) {
        this.userPreferenceRepository = userPreferenceRepository;
    }

    @PostConstruct
    public void init() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getResourceAsStream("/compliments.json");
        compliments = mapper.readValue(is, new TypeReference<Map<String, List<String>>>() {});
    }

    @Override
    public void execute(ProcessContext context) {

        List<String> tags = context.getTags();

        if (tags.contains("unknown")) {
            setResponse(context, "unknown");
            return;
        }

        Set<String> contextTags = new HashSet<>(tags);

        String bestKey = null;
        int maxMatches = 0;

        for (String key : compliments.keySet()) {

            if (key.equals("default") || key.equals("unknown")) continue;

            Set<String> keyTags = new HashSet<>(Arrays.asList(key.split("_")));

            // Special case for combinations with "super_success" or "super_sad"
            // We want e.g., "morning_super_success" to be preferred over "morning_success" if both exist
            // And if "morning_success" is the only one, but we have "super_success", we might just want "super_success" over "morning_success".
            // To do this simply: if context has super_success, we treat keys that only have "success" as a lower priority/invalid match.
            if (contextTags.contains("super_success") && keyTags.contains("success") && !keyTags.contains("super")) {
               continue; // Ignore simple success matches if we are super_success (unless the key explicitly is super_success)
            }
            if (contextTags.contains("super_sad") && keyTags.contains("sad") && !keyTags.contains("super")) {
               continue; // Ignore simple sad matches if we are super_sad
            }

            if (contextTags.containsAll(keyTags)) {
                if (keyTags.size() > maxMatches) {
                    maxMatches = keyTags.size();
                    bestKey = key;
                }
            }
        }

        if (bestKey != null) {
            // Check one more edge case: if bestKey is just a single tag (like "morning"), but we have "super_success"
            // "super_success" is a better fallback than an unrelated single tag.
            if (maxMatches == 1) {
                if (contextTags.contains("super_success") && compliments.containsKey("super_success") && !bestKey.equals("super_success")) {
                    bestKey = "super_success";
                } else if (contextTags.contains("super_sad") && compliments.containsKey("super_sad") && !bestKey.equals("super_sad")) {
                    bestKey = "super_sad";
                }
            }
            setResponse(context, bestKey);
            return;
        }

        if (contextTags.contains("super_success") && compliments.containsKey("super_success")) {
            setResponse(context, "super_success");
            return;
        }

        if (contextTags.contains("success") && compliments.containsKey("success")) {
            setResponse(context, "success");
            return;
        }

        if (contextTags.contains("super_sad") && compliments.containsKey("super_sad")) {
            setResponse(context, "super_sad");
            return;
        }

        if (contextTags.contains("sad") && compliments.containsKey("sad")) {
            setResponse(context, "sad");
            return;
        }

        if (contextTags.contains("neutral") && compliments.containsKey("neutral")) {
            setResponse(context, "neutral");
            return;
        }

        for (String tag : contextTags) {
            if (compliments.containsKey(tag)) {
                setResponse(context, tag);
                return;
            }
        }

        setResponse(context, "default");
    }

    private void setResponse(ProcessContext context, String key) {
        List<String> phrases = compliments.getOrDefault(key, compliments.get("default"));
        
        User user = context.getUserEntity();
        List<UserPreference> preferences = new ArrayList<>();
        if (user != null) {
            preferences = userPreferenceRepository.findByUserId(user.getId());
        }

        Map<String, Integer> weights = new HashMap<>();
        int totalWeight = 0;

        for (String phrase : phrases) {
            String hash = String.valueOf(phrase.hashCode());
            int weight = 10; // Base weight

            if (user != null) {
                for (UserPreference pref : preferences) {
                    if (pref.getPhraseHash().equals(hash)) {
                        if (pref.getScore() > 0) {
                            weight = 30; // Liked phrase
                        } else if (pref.getScore() < 0) {
                            weight = 1;  // Disliked phrase
                        }
                        break;
                    }
                }
            }
            
            weights.put(phrase, weight);
            totalWeight += weight;
        }

        int randomValue = ThreadLocalRandom.current().nextInt(totalWeight);
        int currentWeightSum = 0;
        String selectedPhrase = phrases.get(0);
        
        for (String phrase : phrases) {
            currentWeightSum += weights.get(phrase);
            if (randomValue < currentWeightSum) {
                selectedPhrase = phrase;
                break;
            }
        }

        context.setTemplatePhrase(selectedPhrase);
        context.setPhraseHash(String.valueOf(selectedPhrase.hashCode()));
    }
}