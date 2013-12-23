package net.avh4.scratch.challenge;

import net.avh4.data.log.TransactionLog;

import java.util.List;

public class ViewModel {
    private final ActiveChallengesRepository activeChallengesRepository;
    private final DaysRepository daysRepository;
    private final NewThing newThing;

    public ViewModel(TransactionLog txnLog) {
        activeChallengesRepository = new ActiveChallengesRepository(txnLog);
        daysRepository = new DaysRepository(txnLog);
        newThing = new NewThing(activeChallengesRepository, daysRepository);
    }

    public List<String> activeChallenges() {
        return activeChallengesRepository.getAll();
    }

    public List<String> days(String activeChallengeId) {
        return activeChallengesRepository.days(activeChallengeId);
    }

    public boolean completed(String dayId) {
        return daysRepository.completed(dayId);
    }

    public String dayTitle(String dayId) {
        return daysRepository.title(dayId);
    }

    public boolean challengeCompleted(String challengeId) {
        return newThing.completed(challengeId);
    }

    public String activeChallengeName(String challengeId) {
        return activeChallengesRepository.name(challengeId);
    }
}
