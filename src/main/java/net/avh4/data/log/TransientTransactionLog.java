package net.avh4.data.log;

import java.util.ArrayList;
import java.util.List;

public class TransientTransactionLog implements TransactionLog, TransactionLogCommands {
    private final ArrayList<Transaction> txns = new ArrayList<>();

    @Override
    public void add(String key, String value) {
        synchronized(this) {
            int next = txns.size();
            txns.add(new Transaction(next, key, value));
        }
    }

    @Override
    public List<Transaction> getAll() {
        return txns; // TODO: XXX this is mutable
    }

    @Override
    public List<Transaction> get(int startingIndex) {
        return new ArrayList<>(txns.subList(startingIndex, txns.size()));
    }
}
