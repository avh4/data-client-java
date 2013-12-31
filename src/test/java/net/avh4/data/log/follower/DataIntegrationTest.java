package net.avh4.data.log.follower;

import net.avh4.data.log.follower.fields.*;
import org.junit.Test;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static java.util.Calendar.*;
import static org.fest.assertions.Assertions.assertThat;

public class DataIntegrationTest {
    private TestModel subject = new TestModel();

    @Test
    public void testStringAttribute() throws Exception {
        assertThat(subject.message()).isNull();
        subject = subject.apply(1, "message", "Welcome to Zork!");
        assertThat(subject.message()).isEqualTo("Welcome to Zork!");
        subject = subject.apply(2, "message", "Goodbye from Zork!");
        assertThat(subject.message()).isEqualTo("Goodbye from Zork!");
        subject = subject.apply(3, "message", null);
        assertThat(subject.message()).isNull();
    }

    @Test
    public void testIntegerAttribute() throws Exception {
        assertThat(subject.luckyNumber()).isEqualTo(0);
        subject = subject.apply(1, "lucky number", "7");
        assertThat(subject.luckyNumber()).isEqualTo(7);
        subject = subject.apply(2, "lucky number", "42");
        assertThat(subject.luckyNumber()).isEqualTo(42);
        subject = subject.apply(3, "lucky number", "not a number");
        assertThat(subject.luckyNumber()).isEqualTo(42);
        subject = subject.apply(4, "lucky number", null);
        assertThat(subject.luckyNumber()).isEqualTo(0);
    }

    @Test
    public void testListOfStringsAttribute() throws Exception {
        assertThat(subject.adminEmails()).isEmpty();
        subject = subject.apply(1, "admin emails/1", "bob@example.com");
        assertThat(subject.adminEmails()).containsExactly("bob@example.com");
        subject = subject.apply(2, "admin emails/3", "jan@example.com");
        assertThat(subject.adminEmails()).containsExactly("bob@example.com", "jan@example.com");
        subject = subject.apply(3, "admin emails/2", "rob@example.com");
        assertThat(subject.adminEmails()).containsExactly("bob@example.com", "rob@example.com", "jan@example.com");
        subject = subject.apply(4, "admin emails/1", "bobby@example.com");
        assertThat(subject.adminEmails()).containsExactly("bobby@example.com", "rob@example.com", "jan@example.com");
        subject = subject.apply(5, "admin emails/2", null);
        assertThat(subject.adminEmails()).containsExactly("bobby@example.com", "jan@example.com");
    }

    @Test
    public void testListOfObjectsAttribute() throws Exception {
        assertThat(subject.history()).isEmpty();
        subject = subject.apply(1, "history/1/description", "King Richard comes to power");
        assertThat(subject.history()).hasSize(1);
        assertThat(subject.history().get(0).description()).isEqualTo("King Richard comes to power");
        subject = subject.apply(2, "history/1/date", "1452-10-02");
        assertThat(subject.history()).hasSize(1);
        assertThat(subject.history().get(0).date()).isEqualTo(new GregorianCalendar(1452, OCTOBER, 2).getTime());
        subject = subject.apply(3, "history/3/date", "1485-08-22");
        assertThat(subject.history()).hasSize(2);
        assertThat(subject.history().get(1).date()).isEqualTo(new GregorianCalendar(1485, AUGUST, 22).getTime());
        subject = subject.apply(4, "history/2/date", "1483-06-26");
        assertThat(subject.history()).hasSize(3);
        assertThat(subject.history().get(1).date()).isEqualTo(new GregorianCalendar(1483, JUNE, 26).getTime());
        subject = subject.apply(5, "history/1/description", "Richard is born");
        assertThat(subject.history()).hasSize(3);
        assertThat(subject.history().get(0).description()).isEqualTo("Richard is born");
        subject = subject.apply(6, "history/1", null);
        assertThat(subject.history()).hasSize(2);
        assertThat(subject.history().get(0).date()).isEqualTo(new GregorianCalendar(1483, JUNE, 26).getTime());
        assertThat(subject.history().get(1).date()).isEqualTo(new GregorianCalendar(1485, AUGUST, 22).getTime());
    }

    private static class TestModel extends GenericModel<TestModel> {
        private TestModel() {
            this(HashTreePMap.<String, Field>empty()
                    .plus("message", new StringField())
                    .plus("lucky number", new IntegerField())
                    .plus("admin emails", new StringListField())
                    .plus("history", new ObjectListField<>(new HistoryProvider()))
            );
        }

        private TestModel(PMap<String, Field> fields) {
            super(fields);
        }

        @Override
        protected TestModel newInstance(PMap<String, Field> fields) {
            return new TestModel(fields);
        }

        public String message() {
            return ((StringField) get("message")).value();
        }

        public int luckyNumber() {
            return ((IntegerField) get("lucky number")).value();
        }

        public List<String> adminEmails() {
            return ((StringListField) get("admin emails")).list();
        }

        public List<History> history() {
            return ((ObjectListField<History>) get("history")).list();
        }

        private static class HistoryProvider implements DataProvider<History> {
            @Override
            public History create(int txn, String id) {
                return new History(txn, id);
            }
        }
    }

    private static class History extends GenericModel<History> {
        private History(int txn, String id) {
            this(HashTreePMap.<String, Field>empty()
                    .plus("date", new DateField())
                    .plus("description", new StringField())
            );
        }

        private History(PMap<String, Field> fields) {
            super(fields);
        }

        @Override
        protected History newInstance(PMap<String, Field> fields) {
            return new History(fields);
        }

        public Date date() {
            return ((DateField) get("date")).value();
        }

        public String description() {
            return ((StringField) get("description")).value();
        }
    }
}
