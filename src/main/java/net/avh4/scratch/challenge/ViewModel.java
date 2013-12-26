package net.avh4.scratch.challenge;

import net.avh4.data.log.*;

import java.util.List;

public class ViewModel {
    private final FollowerEngine<ActiveChallengesRepository> activeChallengesRepository;
    private final FollowerEngine<DaysRepository> daysRepository;
    private final NewThing newThing;

    public ViewModel(TransactionLog txnLog) {
        activeChallengesRepository = new LogFollowerEngine<>(txnLog, new ActiveChallengesRepository());
        daysRepository = new LogFollowerEngine<>(txnLog, new DaysRepository());
        newThing = new NewThing(activeChallengesRepository, daysRepository);
    }

    public ViewModel(TransactionBuffer buffer) {
        activeChallengesRepository = new BufferFollowerEngine<>(buffer, new ActiveChallengesRepository());
        daysRepository = new BufferFollowerEngine<>(buffer, new DaysRepository());
        newThing = new NewThing(activeChallengesRepository, daysRepository);
    }

    public List<String> activeChallenges() {
        return activeChallengesRepository.result().getAll();
    }

    public List<String> days(String activeChallengeId) {
        return activeChallengesRepository.result().days(activeChallengeId);
    }

    public boolean completed(String dayId) {
        return daysRepository.result().completed(dayId);
    }

    public String dayTitle(String dayId) {
        return daysRepository.result().title(dayId);
    }

    public boolean challengeCompleted(String challengeId) {
        return newThing.completed(challengeId);
    }

    public String activeChallengeName(String challengeId) {
        return activeChallengesRepository.result().name(challengeId);
    }
}
