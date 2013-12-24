package net.avh4.data.log;

public interface TransactionFollower<F extends TransactionFollower> {
    void process(Transaction txn);

    F fork();
}
