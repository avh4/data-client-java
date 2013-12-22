package net.avh4.scratch.challenge;

public class TransactionLogFollowerEngine {
    protected final TransactionLog txnLog;
    private final TransactionLogFollower follower;
    private int next = 0;

    public TransactionLogFollowerEngine(TransactionLog txnLog, TransactionLogFollower follower) {
        this.txnLog = txnLog;
        this.follower = follower;
    }

    public void pullTransactionLog() {
        synchronized (this) {
            for (Transaction transaction : txnLog.get(next)) {
                follower.process(transaction);
                next = transaction.index;
            }
        }
    }
}
