package net.avh4.scratch.challenge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DaysRepository implements TransactionLogFollower {
    private final TransactionLogFollowerEngine engine;
    private final HashMap<String, String> titles = new HashMap<>();
    private final HashSet<String> completed = new HashSet<>();

    public DaysRepository(TransactionLog txnLog) {
        this.engine = new TransactionLogFollowerEngine(txnLog, this);
    }

    public String title(String dayId) {
        engine.pullTransactionLog();
        return titles.get(dayId);
    }

    public boolean completed(String dayId) {
        engine.pullTransactionLog();
        return completed.contains(dayId);
    }

    @Override
    public void process(Transaction txn) {
        String[] keys = txn.key.split("/");
        if (keys.length < 4) return;
        if (!keys[0].equals("active challenges") || !keys[2].equals("days")) return;
        String key = keys[0] + '/' + keys[1] + '/' + keys[2] + '/' + keys[3];
//        if (!ids.contains(key)) {
//            ids.add(key);
//            days.put(key, new ArrayList<String>());
//        }

        if (keys.length == 5 && keys[4].equals("title")) { // TODO: not tested
            titles.put(key, txn.value);
        }

        if (keys.length == 5 && keys[4].equals("completed")) { // TODO: not tested
            if (txn.value != null) {
                completed.add(key);
            } else {
                completed.remove(key);
            }
        }
    }
}
