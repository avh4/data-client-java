package net.avh4.scratch.challenge;

import java.util.List;

public class NewThing {
    private final ActiveChallengesRepository activeChallengesRepository;
    private final DaysRepository daysRepository;

    public NewThing(ActiveChallengesRepository activeChallengesRepository, DaysRepository daysRepository) {
        this.activeChallengesRepository = activeChallengesRepository;
        this.daysRepository = daysRepository;
    }

    public boolean completed(String challengeId) {
        List<String> dayIds = activeChallengesRepository.days(challengeId);
        for (String dayId : dayIds) {
            boolean dayCompleted = daysRepository.completed(dayId);
            if (!dayCompleted) return false;
        }
        return true;
    }
}
