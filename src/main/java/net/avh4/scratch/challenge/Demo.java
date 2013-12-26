package net.avh4.scratch.challenge;

import net.avh4.data.log.ServerTransactionLog;
import net.avh4.data.log.TransactionBuffer;

public class Demo {
    public static void main(String[] args) {
        ServerTransactionLog server = new ServerTransactionLog("http://localhost:5000", "ChallengeData", "demo");
        TransactionBuffer buffer = new TransactionBuffer(server, server);
        ViewModel model = new ViewModel(buffer);
        Commands commands = new Commands(buffer);

        System.out.println("=== ACTIVE CHALLENGES ===");
        for (String challenge : model.activeChallenges()) {
            System.out.println(challenge + ": " + model.activeChallengeName(challenge));
        }
        System.out.println();

        String nextIncompleteDay = null;

        String challenge = model.activeChallenges().get(0);
        System.out.println("=== DAYS in " + model.activeChallengeName(challenge) + " ===");
        for (String day : model.days(challenge)) {
            boolean completed = model.completed(day);
            System.out.println(day + ": " + (completed ? "[X] " : "[ ] ") + model.dayTitle(day));
            if (!completed && nextIncompleteDay == null) {
                nextIncompleteDay = day;
            }
        }
        System.out.println();

        if (nextIncompleteDay != null) {
            System.out.println(">>> Completing a day: " + model.dayTitle(nextIncompleteDay));
            commands.completed(nextIncompleteDay);
            System.out.println();
        }

        System.out.println("Flushing buffer to server...");
        buffer.flush();
        System.out.println("Done!");
    }
}
