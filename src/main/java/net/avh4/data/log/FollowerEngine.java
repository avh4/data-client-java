package net.avh4.data.log;

public interface FollowerEngine<F extends TransactionFollower<F>> {
    F result();
}
