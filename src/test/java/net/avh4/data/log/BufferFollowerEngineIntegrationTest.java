package net.avh4.data.log;

import org.junit.Test;

import java.util.HashMap;

import static org.fest.assertions.Assertions.assertThat;

public class BufferFollowerEngineIntegrationTest {
    private static final boolean DEBUG = false;

    private static class TestFollower implements TransactionFollower<TestFollower> {
        public final HashMap<String, String> take_firsts;
        public final HashMap<String, String> take_lasts;

        public TestFollower() {
            this(new HashMap<String, String>(), new HashMap<String, String>());
        }

        private TestFollower(HashMap<String, String> take_firsts, HashMap<String, String> take_lasts) {
            this.take_firsts = take_firsts;
            this.take_lasts = take_lasts;
        }

        @Override
        public void process(Transaction txn) {
            if (DEBUG) {
                System.out.println(this + ": " + txn);
            }
            String[] keys = txn.key.split("/");
            switch (keys[0]) {
                case "take_first":
                    if (take_firsts.containsKey(keys[1])) return;
                    take_firsts.put(keys[1], txn.value);
                    break;
                case "take_last":
                    take_lasts.put(keys[1], txn.value);
                    break;
                default:
                    throw new RuntimeException("Unexpected key: " + txn);
            }
        }

        @Override
        public TestFollower fork() {
            TestFollower testFollower = new TestFollower(new HashMap<>(take_firsts), new HashMap<>(take_lasts));
            if (DEBUG) {
                System.out.println(testFollower + ": forked from " + this);
            }
            return testFollower;
        }
    }

    @Test
    public void testBufferRollback() {
        TransientTransactionLog master = new TransientTransactionLog();
        master.add("take_first/A", "master");
        master.add("take_last/A", "master");
        TransactionBuffer buffer1 = new TransactionBuffer(master, master);
        TransactionBuffer buffer2 = new TransactionBuffer(master, master);

        BufferFollowerEngine<TestFollower> engine1 = new BufferFollowerEngine<>(buffer1, new TestFollower());

        assertThat(engine1.result().take_firsts.get("A")).isEqualTo("master");
        assertThat(engine1.result().take_lasts.get("A")).isEqualTo("master");

        buffer1.add("take_last/A", "client1");
        assertThat(engine1.result().take_lasts.get("A")).isEqualTo("client1");

        buffer1.flush();
        assertThat(engine1.result().take_lasts.get("A")).isEqualTo("client1");

        buffer1.add("take_first/B", "client1");
        buffer2.add("take_first/B", "client2");
        assertThat(engine1.result().take_firsts.get("B")).isEqualTo("client1");
        buffer2.flush();
        buffer1.flush();
        assertThat(engine1.result().take_firsts.get("B")).isEqualTo("client2");
    }
}
