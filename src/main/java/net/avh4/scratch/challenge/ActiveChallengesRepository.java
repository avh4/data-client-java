package net.avh4.scratch.challenge;

import net.avh4.data.log.Transaction;
import net.avh4.data.log.TransactionFollower;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActiveChallengesRepository implements TransactionFollower<ActiveChallengesRepository> {
    // TODO: rewrite using persistent data structures
    private final ArrayList<String> ids;
    private final HashMap<String, String> names;
    private final HashMap<String, ArrayList<String>> days;

    public ActiveChallengesRepository() {
        this(new ArrayList<String>(), new HashMap<String, String>(), new HashMap<String, ArrayList<String>>());
    }

    private ActiveChallengesRepository(ArrayList<String> ids, HashMap<String, String> names,
                                       HashMap<String, ArrayList<String>> days) {
        this.ids = ids;
        this.names = names;
        this.days = days;
    }

    public List<String> getAll() {
        return ids; // TODO: XXX: this is mutable
    }

    public String name(String challengeId) {
        return names.get(challengeId);
    }

    public List<String> days(String activeChallengeId) {
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

    @Override
    public ActiveChallengesRepository fork() {
        return new ActiveChallengesRepository(new ArrayList<>(ids), new HashMap<>(names), new HashMap<>(days));
    }
}
