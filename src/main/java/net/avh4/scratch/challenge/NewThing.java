package net.avh4.scratch.challenge;

import net.avh4.data.log.FollowerEngine;
import net.avh4.data.log.LogFollowerEngine;

import java.util.List;

public class NewThing {
    private final FollowerEngine<ActiveChallengesRepository> activeChallengesRepository;
    private final FollowerEngine<DaysRepository> daysRepository;

    public NewThing(FollowerEngine<ActiveChallengesRepository> activeChallengesRepository,
                    FollowerEngine<DaysRepository> daysRepository) {
        this.activeChallengesRepository = activeChallengesRepository;
        this.daysRepository = daysRepository;
    }

    public boolean completed(String challengeId) {
        List<String> dayIds = activeChallengesRepository.result().days(challengeId);
        for (String dayId : dayIds) {
            boolean dayCompleted = daysRepository.result().completed(dayId);
            if (!dayCompleted) return false;
        }
        return true;
    }
}
