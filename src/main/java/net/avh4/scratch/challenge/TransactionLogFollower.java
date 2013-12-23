package net.avh4.scratch.challenge;

import net.avh4.data.log.Transaction;

public interface TransactionLogFollower {
    void process(Transaction txn);
}
