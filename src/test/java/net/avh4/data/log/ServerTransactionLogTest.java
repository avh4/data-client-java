package net.avh4.data.log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.simpleframework.transport.connect.Connection;

import static org.fest.assertions.Assertions.assertThat;

public class ServerTransactionLogTest {
    public static final int PORT = 9876;
    private ServerTransactionLog subject;
    private Connection connection;

    @Before
    public void setUp() throws Exception {
        connection = new SimpleServer().connect(PORT);
        subject = new ServerTransactionLog("http://localhost:" + PORT, "UNIT-TESTS", "UNIT-TESTS-USER");
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void startsEmpty() {
        assertThat(subject.get(0)).isEmpty();
    }

    @Test
    public void addingATransaction_returnsTheTransaction() {
        subject.add("key1", "value1");
        assertThat(subject.get(0)).containsExactly(new Transaction(1, "key1", "value1"));
    }

    @Test
    public void get_returnsNewTransactions() {
        subject.add("key1", "value1");
        subject.add("key2", "value2");
        assertThat(subject.get(1)).containsExactly(new Transaction(2, "key2", "value2"));
    }

    @Test
    public void get_withNoNewTransactions_returnsEmpty() {
        subject.add("key1", "value1");
        assertThat(subject.get(1)).isEmpty();
    }
}
