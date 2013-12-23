package net.avh4.scratch.challenge;

import net.avh4.data.log.TransactionLog;
import net.avh4.data.log.TransactionLogCommands;

import java.util.Date;

public class Commands {
    private final TransactionLogCommands txnLog;

    public Commands(TransactionLogCommands txnLog) {
        this.txnLog = txnLog;
    }

    public void completed(String dayId) { // TODO: not tested
        txnLog.add(dayId + "/completed", new Date().toString());
    }
}
