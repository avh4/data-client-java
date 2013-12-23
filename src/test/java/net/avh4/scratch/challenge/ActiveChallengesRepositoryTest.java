package net.avh4.scratch.challenge;

import net.avh4.data.log.Transaction;
import net.avh4.data.log.TransactionLog;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.fest.assertions.Assertions.assertThat;

public class ActiveChallengesRepositoryTest {
    private ActiveChallengesRepository subject;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new ActiveChallengesRepository();
    }

    @Test
    public void getAll_withNoTransactions_isEmpty() {
        assertThat(subject.getAll()).isEmpty();
    }

    @Test
    public void getAll_withOne_returnsIt() {
        subject.process(new Transaction(0, "active challenges/TEST-1/name", "Plank Challenge"));
        assertThat(subject.getAll()).containsExactly("active challenges/TEST-1");
    }

     // TODO: remove an active challenge

    @Test
    public void name_whenKnown_returnsIt() {
        subject.process(new Transaction(0, "active challenges/TEST-1/name", "Plank Challenge"));
        assertThat(subject.name("active challenges/TEST-1")).isEqualTo("Plank Challenge");
    }

    @Test
    public void name_whenUnknown_returnsNull() {
        assertThat(subject.name("active challenges/TEST-1")).isNull();
    }

    @Test
    public void days_withNoDays_isEmpty() {
        subject.process(new Transaction(0, "active challenges/TEST-1/name", "Plank Challenge"));
        assertThat(subject.days("active challenges/TEST-1")).isEmpty();
    }

    @Test
    public void days_withOneDay_returnsIt() {
        subject.process(new Transaction(0, "active challenges/TEST-1/name", "Plank Challenge"));
        subject.process(new Transaction(1, "active challenges/TEST-1/days/1", "20 seconds"));
        assertThat(subject.days("active challenges/TEST-1")).containsExactly("active challenges/TEST-1/days/1");
    }

    @Test
    public void days_withManyDays_returnsThem() {
        subject.process(new Transaction(0, "active challenges/TEST-1/name", "Plank Challenge"));
        subject.process(new Transaction(1, "active challenges/TEST-1/days/1", "20 seconds"));
        subject.process(new Transaction(2, "active challenges/TEST-1/days/2", "30 seconds"));
        assertThat(subject.days("active challenges/TEST-1")).containsExactly(
                "active challenges/TEST-1/days/1", "active challenges/TEST-1/days/2");
    }

    // TODO: remove a day
}
