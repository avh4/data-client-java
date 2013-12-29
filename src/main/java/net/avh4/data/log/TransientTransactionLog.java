package net.avh4.data.log;

import java.util.ArrayList;
import java.util.List;

public class TransientTransactionLog implements TransactionLog, TransactionLogCommands, TransactionLogBulkCommands {
    private final ArrayList<Transaction> txns = new ArrayList<>();
    private int count = 0;

    @Override
    public void add(String key, String value) {
        synchronized (this) {
            txns.add(new Transaction(++count, key, value));
        }
    }

    @Override
    public List<Transaction> get(int startingIndex) {
        return new ArrayList<>(txns.subList(startingIndex, txns.size()));
    }

    @Override
    public void addAll(List<Transaction> txns) {
        for (Transaction txn : txns) {
            add(txn.key, txn.value);
        }
    }
}
