package net.avh4.scratch.challenge;

import net.avh4.data.log.Transaction;
import net.avh4.data.log.TransactionLog;

public class TransactionLogFollowerEngine<F extends TransactionLogFollower> {
    protected final TransactionLog txnLog;
    private final F follower;
    private int next = 0;

    public TransactionLogFollowerEngine(TransactionLog txnLog, F follower) {
        this.txnLog = txnLog;
        this.follower = follower;
    }

    protected void pullTransactionLog() {
        synchronized (this) {
            for (Transaction transaction : txnLog.get(next)) {
                follower.process(transaction);
                next = transaction.index;
            }
        }
    }

    public F result() {
        pullTransactionLog();
        return follower;
    }
}
