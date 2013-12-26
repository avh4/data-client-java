package net.avh4.data.log;

public class LogFollowerEngine<F extends TransactionFollower<F>> implements FollowerEngine<F> {
    protected final TransactionLog txnLog;
    private final F follower;
    private int last = 0;

    public LogFollowerEngine(TransactionLog txnLog, F follower) {
        this.txnLog = txnLog;
        this.follower = follower;
    }

    protected void pullTransactionLog() {
        // TODO: test this
        synchronized (this) {
            for (Transaction transaction : txnLog.get(last)) {
                follower.process(transaction);
                last = transaction.index;
            }
        }
    }

    public F result() {
        pullTransactionLog();
        return follower;
    }
}
