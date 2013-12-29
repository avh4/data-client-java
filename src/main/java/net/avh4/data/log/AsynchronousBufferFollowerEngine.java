package net.avh4.data.log;

public class AsynchronousBufferFollowerEngine<F extends TransactionFollower<F>> implements FollowerEngine<F> {
    private final TransactionBuffer buffer;
    private final F masterFollower;
    private int lastCommitted = 0;
    private int lastPending = 0;
    private F forkFollower;

    public AsynchronousBufferFollowerEngine(TransactionBuffer buffer, F follower) {
        this.buffer = buffer;
        this.masterFollower = follower;
    }

    public void sync() {
        synchronized (this) {
            for (Transaction txn : buffer.getCommitted(lastCommitted)) {
                masterFollower.process(txn);
                lastCommitted = txn.index;
                lastPending = 0;
                forkFollower = null;
            }
            for (Transaction txn : buffer.getPending(lastCommitted, lastPending)) {
                if (forkFollower == null) {
                    forkFollower = masterFollower.fork();
                }
                forkFollower.process(txn);
                lastPending = txn.index;
            }
        }
    }

    @Override
    public F result() {
        if (forkFollower != null) return forkFollower;
        return masterFollower;
    }
}
