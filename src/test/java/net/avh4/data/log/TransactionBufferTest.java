package net.avh4.data.log;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class TransactionBufferTest {

    private TransientTransactionLog master;
    private TransactionBuffer subject;

    @Before
    public void setUp() throws Exception {
        master = new TransientTransactionLog();
        master.add("k1", "v1");
        master.add("k2", "v2");
        subject = new TransactionBuffer(master, master);
    }

    @Test
    public void getCommitted_shouldGiveTransactionsFromMaster() {
        TransactionBuffer.Updates updates = subject.get(0, 0);
        assertThat(updates.committed).containsExactly(
                new Transaction(1, "k1", "v1"),
                new Transaction(2, "k2", "v2")
        );
    }

    @Test
    public void getPending_returnsPendingTransactions() {
        subject.get(0, 0);
        subject.add("k10", "v10");
        assertThat(subject.getPending(2, 0)).containsExactly(
                new Transaction(1, "k10", "v10")
        );
    }

    @Test
    public void getPending_withNoPendingTransactions_returnsEmpty() {
        subject.get(0, 0);
        assertThat(subject.getPending(2, 0)).isEmpty();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getPending_withUnacknowledgedCommittedTransactions_throws() {
        subject.get(0, 0);
        subject.getPending(0, 0);
    }

    @Test
    public void flush_addsPendingTransactionsToMaster() {
        subject.add("k10", "v10");
        subject.flush();
        assertThat(master.get(0)).containsExactly(
                new Transaction(1, "k1", "v1"),
                new Transaction(2, "k2", "v2"),
                new Transaction(3, "k10", "v10")
        );
    }

    @Test
    public void flush_createsNewCommittedTransactions() {
        subject.add("k10", "v10");
        subject.flush();
        TransactionBuffer.Updates updates = subject.get(2, 0);
        assertThat(updates.committed).containsExactly(
                new Transaction(3, "k10", "v10")
        );
    }

    @Test
    public void flush_clearsPendingTransactions() {
        subject.get(0, 0);
        subject.add("k10", "v10");
        subject.flush();
        subject.get(2, 0);
        assertThat(subject.getPending(3, 0)).isEmpty();
    }
}
