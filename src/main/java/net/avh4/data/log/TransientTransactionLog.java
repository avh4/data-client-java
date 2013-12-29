package net.avh4.data.log;

import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.util.List;

public class TransientTransactionLog implements TransactionLog, TransactionLogCommands, TransactionLogBulkCommands {
    private TreePVector<Transaction> txns = TreePVector.empty();
    private int count = 0;

    @Override
    public void add(String key, String value) {
        synchronized (this) {
            txns = txns.plus(new Transaction(++count, key, value));
        }
    }

    @Override
    public PVector<Transaction> get(int startingIndex) {
        return txns.subList(startingIndex, txns.size());
    }

    @Override
    public void addAll(List<Transaction> txns) {
        for (Transaction txn : txns) {
            add(txn.key, txn.value);
        }
    }
}
