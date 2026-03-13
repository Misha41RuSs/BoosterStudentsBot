package com.tg.boosterbot.service.filters;

import com.tg.boosterbot.entity.User;
import com.tg.boosterbot.entity.UserStats;
import com.tg.boosterbot.model.ProcessContext;
import com.tg.boosterbot.repository.UserRepository;
import com.tg.boosterbot.repository.UserStatsRepository;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class NameExtractorFilter implements Filter {

    private final UserRepository userRepository;
    private final UserStatsRepository userStatsRepository;

    public NameExtractorFilter(UserRepository userRepository, UserStatsRepository userStatsRepository) {
        this.userRepository = userRepository;
        this.userStatsRepository = userStatsRepository;
    }

    @Override
    public void execute(ProcessContext context) {
        Long chatId = context.getChatId();

        User user = userRepository.findByChatId(chatId).orElseGet(() -> {
            User newUser = User.builder()
                    .chatId(chatId)
                    .firstName(context.getUserName())
                    .build();
            userRepository.save(newUser);

            UserStats stats = UserStats.builder()
                    .user(newUser)
                    .build();
            userStatsRepository.save(stats);

            newUser.setStats(stats);
            return newUser;
        });

        if (context.getUserName() != null && !context.getUserName().equals(user.getFirstName())) {
            user.setFirstName(context.getUserName());
            userRepository.save(user);
        }

        context.setUserEntity(user);
    }
}
