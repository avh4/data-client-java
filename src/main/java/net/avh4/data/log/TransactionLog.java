package net.avh4.data.log;

import org.pcollections.PVector;

public interface TransactionLog {
    PVector<Transaction> get(int last);
}
