package net.avh4.scratch.challenge;

import java.util.ArrayList;
import java.util.List;

public class TransactionLog {
    private final ArrayList<Transaction> txns = new ArrayList<>();

    public void add(String key, String value) {
        synchronized(this) {
            int next = txns.size();
            txns.add(new Transaction(next, key, value));
        }
    }

    public List<Transaction> getAll() {
        return txns; // TODO: XXX this is mutable
    }

    public List<Transaction> get(int startingIndex) {
        return new ArrayList<>(txns.subList(startingIndex, txns.size()));
    }
}
