package net.avh4.data.log;

public class BufferFollowerEngine<F extends TransactionFollower<F>> {
    private final TransactionBuffer buffer;
    private final F masterFollower;
    private int nextCommitted = 0;
    private int nextPending = 0;
    private F forkFollower;

    public BufferFollowerEngine(TransactionBuffer buffer, F follower) {
        this.buffer = buffer;
        this.masterFollower = follower;
    }

    protected void pullTransactionLog() {
        synchronized (this) {
            for (Transaction txn : buffer.getCommitted(nextCommitted)) {
                masterFollower.process(txn);
                nextCommitted = txn.index + 1;
                nextPending = 0;
                forkFollower = null;
            }
            for (Transaction txn : buffer.getPending(nextCommitted, nextPending)) {
                if (forkFollower == null) {
                    forkFollower = masterFollower.fork();
                }
                forkFollower.process(txn);
                nextPending = txn.index + 1;
            }
        }
    }

    public F result() {
        pullTransactionLog();
        if (forkFollower != null) return forkFollower;
        return masterFollower;
    }
}
