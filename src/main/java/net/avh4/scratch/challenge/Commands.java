package net.avh4.scratch.challenge;

import java.util.Date;

public class Commands {
    private final TransactionLog txnLog;

    public Commands(TransactionLog txnLog) {
        this.txnLog = txnLog;
    }

    public void completed(String dayId) { // TODO: not tested
        txnLog.add(dayId + "/completed", new Date().toString());
    }
}
