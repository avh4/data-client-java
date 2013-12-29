package net.avh4.data.log;

import org.pcollections.PVector;
import org.pcollections.TreePVector;

public class TransactionBuffer implements TransactionLogCommands {
    private final TransactionLog master;
    private final TransactionLogBulkCommands masterCommands;
    private TransientTransactionLog pending = new TransientTransactionLog();
    private int lastCommittedRequest;
    private final Object addLock = new Object();
    private final Object flushLock = new Object();
    private int masterLockedAt;

    public class Updates {
        public final PVector<Transaction> committed;
        public final PVector<Transaction> pending;

        public Updates(PVector<Transaction> committed, PVector<Transaction> pending) {
            this.committed = committed;
            this.pending = pending;
        }
    }

    public TransactionBuffer(TransactionLog master, TransactionLogBulkCommands masterCommands) {
        this.master = master;
        this.masterCommands = masterCommands;
    }

    public synchronized void flush() {
        synchronized (flushLock) {
            PVector<Transaction> transactionsToWrite;
            int lastWritten;
            synchronized (addLock) {
                transactionsToWrite = pending.get(0);
                if (transactionsToWrite.isEmpty())
                    return;
                lastWritten = transactionsToWrite.get(transactionsToWrite.size() - 1).index;
                masterLockedAt = lastCommittedRequest;
            }
            masterCommands.addAll(transactionsToWrite);
            synchronized (addLock) {
                PVector<Transaction> notWritten = pending.get(lastWritten);
                pending = new TransientTransactionLog();
                pending.addAll(notWritten);
                masterLockedAt = 0;
            }
        }
    }

    public Updates get(int lastCommitted, int lastPending) {
        PVector<Transaction> committed;
        PVector<Transaction> pendings;
        synchronized (addLock) {
            if (masterLockedAt != 0) {
                if (lastCommitted != masterLockedAt) {
                    throw new RuntimeException("Internal error: master is locked at " + masterLockedAt +
                            ", but caller thinks there are more recent records (" + lastCommitted + ")");
                }
                committed = TreePVector.empty();
            } else {
                committed = master.get(lastCommitted);
            }

            if (!committed.isEmpty()) {
                lastCommittedRequest = committed.get(committed.size() - 1).index;
                pendings = pending.get(0);
            } else {
                pendings = pending.get(lastPending);
            }
        }
        return new Updates(committed, pendings);
    }

    public PVector<Transaction> getPending(int lastCommitted, int lastPending) {
        synchronized (addLock) {
            if (lastCommitted != lastCommittedRequest) {
                throw new IllegalArgumentException("Must acknowledge all committed transactions when requesting pending " +
                        "transactions: " + lastCommitted + " (passed) != " + lastCommittedRequest + " (expected)");
            }
            return pending.get(lastPending);
        }
    }

    @Override
    public void add(String key, String value) {
        synchronized (addLock) {
            pending.add(key, value);
        }
    }
}
