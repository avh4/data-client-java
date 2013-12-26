package net.avh4.data.log;

import org.junit.Test;

import java.util.HashMap;

import static org.fest.assertions.Assertions.assertThat;

public class BufferFollowerEngineIntegrationTest {
    private static final boolean DEBUG = false;

    private static class TestFollower implements TransactionFollower<TestFollower> {
        private final HashMap<String, String> initialValues;
        private final HashMap<String, String> mostRecentValues;

        public TestFollower() {
            this(new HashMap<String, String>(), new HashMap<String, String>());
        }

        private TestFollower(HashMap<String, String> initialValues, HashMap<String, String> mostRecentValues) {
            this.initialValues = initialValues;
            this.mostRecentValues = mostRecentValues;
        }

        public String initialValue(String id) {
            return initialValues.get(id);
        }

        public String mostRecentValue(String id) {
            return mostRecentValues.get(id);
        }

        @Override
        public void process(Transaction txn) {
            if (DEBUG) {
                System.out.println(this + ": " + txn);
            }
            String[] keys = txn.key.split("/");
            if (keys.length == 2 && keys[1].equals("name")) {
                if (!initialValues.containsKey(keys[0]))
                    initialValues.put(keys[0], txn.value);
                mostRecentValues.put(keys[0], txn.value);
            } else {
                throw new RuntimeException("Unexpected key: " + txn);
            }
        }

        @Override
        public TestFollower fork() {
            TestFollower testFollower = new TestFollower(new HashMap<>(initialValues), new HashMap<>(mostRecentValues));
            if (DEBUG) {
                System.out.println(testFollower + ": forked from " + this);
            }
            return testFollower;
        }
    }

    @Test
    public void testBufferRollback() {
        TransientTransactionLog master = new TransientTransactionLog();
        master.add("A/name", "master");
        TransactionBuffer buffer1 = new TransactionBuffer(master, master);
        TransactionBuffer buffer2 = new TransactionBuffer(master, master);

        FollowerEngine<TestFollower> engine1 = new BufferFollowerEngine<>(buffer1, new TestFollower());

        assertThat(engine1.result().initialValue("A")).isEqualTo("master");
        assertThat(engine1.result().mostRecentValue("A")).isEqualTo("master");

        buffer1.add("A/name", "client1");
        assertThat(engine1.result().mostRecentValue("A")).isEqualTo("client1");

        buffer1.flush();
        assertThat(engine1.result().mostRecentValue("A")).isEqualTo("client1");

        buffer1.add("B/name", "client1");
        buffer2.add("B/name", "client2");
        assertThat(engine1.result().initialValue("B")).isEqualTo("client1");
        buffer2.flush();
        assertThat(engine1.result().initialValue("B")).isEqualTo("client2");
        buffer1.flush();
        assertThat(engine1.result().initialValue("B")).isEqualTo("client2");
    }
}
