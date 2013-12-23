package net.avh4.scratch.challenge;

import java.util.List;

public class NewThing {
    private final TransactionLogFollowerEngine<ActiveChallengesRepository> activeChallengesRepository;
    private final TransactionLogFollowerEngine<DaysRepository> daysRepository;

    public NewThing(TransactionLogFollowerEngine<ActiveChallengesRepository> activeChallengesRepository,
                    TransactionLogFollowerEngine<DaysRepository> daysRepository) {
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
