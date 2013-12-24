package net.avh4.data.log;

import java.util.List;

public class TransactionBuffer implements TransactionLogCommands {
    private final TransactionLog master;
    private final TransactionLogCommands masterCommands;
    private TransientTransactionLog pending = new TransientTransactionLog();

    public TransactionBuffer(TransactionLog master, TransactionLogCommands masterCommands) {
        this.master = master;
        this.masterCommands = masterCommands;
    }

    public void flush() {
        for (Transaction transaction : pending.getAll()) {
            masterCommands.add(transaction.key, transaction.value);
        }
        pending = new TransientTransactionLog();
    }

    public List<Transaction> getCommitted(int startingIndex) {
        return master.get(startingIndex);
    }

    public List<Transaction> getPending(int committedIndex, int startingIndex) {
        int count = master.count();
        if (committedIndex != count) {
            throw new IllegalArgumentException("Flush committed transactions before requesting pending transactions: " +
                    "committedIndex (" + committedIndex + ") != " + count);
        }
        return pending.get(startingIndex);
    }

    @Override
    public void add(String key, String value) {
        pending.add(key, value);
    }
}
