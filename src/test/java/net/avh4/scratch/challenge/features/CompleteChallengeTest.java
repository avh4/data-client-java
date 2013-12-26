package net.avh4.scratch.challenge.features;

import net.avh4.data.log.ServerTransactionLog;
import net.avh4.data.log.SimpleServer;
import net.avh4.scratch.challenge.Commands;
import net.avh4.scratch.challenge.ViewModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.simpleframework.transport.connect.Connection;

import static org.fest.assertions.Assertions.assertThat;

public class CompleteChallengeTest {
    private static final int PORT = 9632;

    private Connection connection;
    private ServerTransactionLog txnLog;
    private ViewModel ui;
    private Commands commands;

    @Before
    public void setUp() throws Exception {
        SimpleServer server = new SimpleServer();
        connection = server.connect(PORT);

        txnLog = new ServerTransactionLog("http://localhost:" + PORT + "/apps/UNIT-TESTS", "UNIT-TESTS-USER");
        ui = new ViewModel(txnLog);
        commands = new Commands(txnLog);
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void successfulCompletion() {
        setupActiveChallenge();

        String challengeId = "active challenges/TEST-1";
        assertThat(ui.activeChallenges()).containsExactly(challengeId);
        assertThat(ui.activeChallengeName(challengeId)).isEqualTo("Plank Challenge");

        for (int i = 1; i <= 30; i++) {
            assertThat(ui.challengeCompleted(challengeId)).isFalse();
            String dayId = challengeId + "/days/" + i;
            assertThat(ui.days(challengeId)).contains(dayId);
            assertThat(ui.dayTitle(dayId)).isNotNull();
            commands.completed(dayId);
            assertThat(ui.completed(dayId)).isTrue();
        }

        assertThat(ui.challengeCompleted(challengeId)).isTrue();
    }

    private void setupActiveChallenge() {
        txnLog.add("active challenges/TEST-1/name", "Plank Challenge");
        txnLog.add("active challenges/TEST-1/days/1/title", "20 seconds");
        txnLog.add("active challenges/TEST-1/days/2/title", "20 seconds");
        txnLog.add("active challenges/TEST-1/days/3/title", "30 seconds");
        txnLog.add("active challenges/TEST-1/days/4/title", "30 seconds");
        txnLog.add("active challenges/TEST-1/days/5/title", "40 seconds");
        txnLog.add("active challenges/TEST-1/days/6/title", "Rest");
        txnLog.add("active challenges/TEST-1/days/7/title", "45 seconds");
        txnLog.add("active challenges/TEST-1/days/8/title", "45 seconds");
        txnLog.add("active challenges/TEST-1/days/9/title", "1 minute");
        txnLog.add("active challenges/TEST-1/days/10/title", "1 minute");
        txnLog.add("active challenges/TEST-1/days/11/title", "1 minute");
        txnLog.add("active challenges/TEST-1/days/12/title", "1.5 minutes");
        txnLog.add("active challenges/TEST-1/days/13/title", "Rest");
        txnLog.add("active challenges/TEST-1/days/14/title", "1.5 minutes");
        txnLog.add("active challenges/TEST-1/days/15/title", "1.5 minutes");
        txnLog.add("active challenges/TEST-1/days/16/title", "2 minutes");
        txnLog.add("active challenges/TEST-1/days/17/title", "2 minutes");
        txnLog.add("active challenges/TEST-1/days/18/title", "2.5 minutes");
        txnLog.add("active challenges/TEST-1/days/19/title", "Rest");
        txnLog.add("active challenges/TEST-1/days/20/title", "2.5 minutes");
        txnLog.add("active challenges/TEST-1/days/21/title", "2.5 minutes");
        txnLog.add("active challenges/TEST-1/days/22/title", "3 minutes");
        txnLog.add("active challenges/TEST-1/days/23/title", "3 minutes");
        txnLog.add("active challenges/TEST-1/days/24/title", "3.5 minutes");
        txnLog.add("active challenges/TEST-1/days/25/title", "3.5 minutes");
        txnLog.add("active challenges/TEST-1/days/26/title", "Rest");
        txnLog.add("active challenges/TEST-1/days/27/title", "4 minutes");
        txnLog.add("active challenges/TEST-1/days/28/title", "4 minutes");
        txnLog.add("active challenges/TEST-1/days/29/title", "4.5 minutes");
        txnLog.add("active challenges/TEST-1/days/30/title", "5 minutes");
    }
}
