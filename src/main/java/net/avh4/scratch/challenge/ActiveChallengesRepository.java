package net.avh4.scratch.challenge;

import net.avh4.data.log.Transaction;
import net.avh4.data.log.TransactionLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActiveChallengesRepository implements TransactionLogFollower {
    private final TransactionLogFollowerEngine engine;
    private final ArrayList<String> ids = new ArrayList<>();
    private final HashMap<String, String> names = new HashMap<>();
    private final HashMap<String, ArrayList<String>> days = new HashMap<>();

    public ActiveChallengesRepository(TransactionLog txnLog) {
        this.engine = new TransactionLogFollowerEngine(txnLog, this);
    }

    public List<String> getAll() {
        engine.pullTransactionLog();
        return ids; // TODO: XXX: this is mutable
    }

    public String name(String challengeId) {
        engine.pullTransactionLog();
        return names.get(challengeId);
    }

    public List<String> days(String activeChallengeId) {
        engine.pullTransactionLog();
        return days.get(activeChallengeId); // TODO: XXX: this is mutable
    }

    @Override
    public void process(Transaction txn) {
        String[] keys = txn.key.split("/");
        if (keys.length < 2) return;
        if (!keys[0].equals("active challenges")) return;
        String key = keys[0] + '/' + keys[1];
        if (!ids.contains(key)) {
            ids.add(key);
            days.put(key, new ArrayList<String>());
        }

        if (keys.length == 3 && keys[2].equals("name")) {
            names.put(key, txn.value);
        }

        if (keys.length >= 4 && keys[2].equals("days")) {
            String dayKey = key + '/' + keys[2] + '/' + keys[3];
            days.get(key).add(dayKey);
        }
    }
}
