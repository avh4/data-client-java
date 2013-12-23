package net.avh4.data.log;

import java.util.List;

public interface TransactionLog {
    List<Transaction> getAll();

    List<Transaction> get(int startingIndex);
}
