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
        for (Transaction transaction : pending.get(0)) {
            masterCommands.add(transaction.key, transaction.value);
        }
        pending = new TransientTransactionLog();
    }

    public List<Transaction> getCommitted(int last) {
        return master.get(last);
    }

    public List<Transaction> getPending(int lastCommitted, int last) {
        int count = master.count();
        if (lastCommitted != count) {
            throw new IllegalArgumentException("Flush committed transactions before requesting pending transactions: " +
                    "committedIndex (" + lastCommitted + ") != " + count);
        }
        return pending.get(last);
    }

    @Override
    public void add(String key, String value) {
        pending.add(key, value);
    }
}
