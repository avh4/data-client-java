package net.avh4.data.log;

import java.util.List;

public class TransactionBuffer implements TransactionLogCommands {
    private final TransactionLog master;
    private final TransactionLogCommands masterCommands;
    private TransientTransactionLog pending = new TransientTransactionLog();
    private int lastCommittedRequest;

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
        List<Transaction> transactions = master.get(last);
        if (!transactions.isEmpty()) {
            lastCommittedRequest = transactions.get(transactions.size() - 1).index;
        }
        return transactions;
    }

    public List<Transaction> getPending(int lastCommitted, int last) {
        if (lastCommitted != lastCommittedRequest) {
            throw new IllegalArgumentException("Must acknowledge all committed transactions when requesting pending " +
                    "transactions: " + lastCommitted + " (passed) != " + lastCommittedRequest + " (expected)");
        }
        return pending.get(last);
    }

    @Override
    public void add(String key, String value) {
        pending.add(key, value);
    }
}
