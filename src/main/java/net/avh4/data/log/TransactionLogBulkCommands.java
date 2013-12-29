package net.avh4.data.log;

import java.util.List;

public interface TransactionLogBulkCommands {
    public void addAll(List<Transaction> txns);
}
