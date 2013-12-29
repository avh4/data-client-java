package net.avh4.data.log;

import org.pcollections.PVector;

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
            TransactionBuffer.Updates updates = buffer.get(lastCommitted, lastPending);
            processCommitted(updates);
            processPending(updates.pending);
        }
    }

    private void syncPending() {
        synchronized (this) {
            PVector<Transaction> pending = buffer.getPending(lastCommitted, lastPending);
            processPending(pending);
        }
    }

    private void processPending(PVector<Transaction> pending) {
        for (Transaction txn : pending) {
            if (forkFollower == null) {
                forkFollower = masterFollower.fork();
            }
            forkFollower.process(txn);
            lastPending = txn.index;
        }
    }

    private void processCommitted(TransactionBuffer.Updates updates) {
        for (Transaction txn : updates.committed) {
            masterFollower.process(txn);
            lastCommitted = txn.index;
            lastPending = 0;
            forkFollower = null;
        }
    }

    @Override
    public F result() {
        syncPending();
        if (forkFollower != null) return forkFollower;
        return masterFollower;
    }
}
