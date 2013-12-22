package net.avh4.scratch.challenge;

public interface TransactionLogFollower {
    void process(Transaction txn);
}
