package net.avh4.scratch.challenge;

import net.avh4.data.log.Transaction;
import net.avh4.data.log.TransactionFollower;

import java.util.HashMap;
import java.util.HashSet;

public class DaysRepository implements TransactionFollower<DaysRepository> {
    // TODO: rewrite using persistent data structures
    private final HashMap<String, String> titles;
    private final HashSet<String> completed;

    public DaysRepository() {
        this(new HashMap<String, String>(), new HashSet<String>());
    }

    private DaysRepository(HashMap<String, String> titles, HashSet<String> completed) {
        this.titles = titles;
        this.completed = completed;
    }

    public String title(String dayId) {
        return titles.get(dayId);
    }

    public boolean completed(String dayId) {
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

    @Override
    public DaysRepository fork() {
        return new DaysRepository(new HashMap<>(titles), new HashSet<>(completed));
    }
}
