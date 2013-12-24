package net.avh4.data.log;

public class LogFollowerEngine<F extends TransactionFollower> {
    protected final TransactionLog txnLog;
    private final F follower;
    private int next = 0;

    public LogFollowerEngine(TransactionLog txnLog, F follower) {
        this.txnLog = txnLog;
        this.follower = follower;
    }

    protected void pullTransactionLog() {
        // TODO: test this
        synchronized (this) {
            for (Transaction transaction : txnLog.get(next)) {
                follower.process(transaction);
                next = transaction.index + 1;
            }
        }
    }

    public F result() {
        pullTransactionLog();
        return follower;
    }
}
