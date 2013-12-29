package net.avh4.data.log;

public class SynchronousBufferFollowerEngine<F extends TransactionFollower<F>>
        extends AsynchronousBufferFollowerEngine<F> {
    public SynchronousBufferFollowerEngine(TransactionBuffer buffer, F follower) {
        super(buffer, follower);
    }

    @Override
    public F result() {
        super.sync();
        return super.result();
    }
}
