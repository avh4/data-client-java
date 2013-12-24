package net.avh4.data.log;

import java.util.List;

public interface TransactionLog {
    int count();

    List<Transaction> getAll();

    List<Transaction> get(int startingIndex);
}
