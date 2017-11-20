package com.github.anthony_o.swingifts.service;

import com.github.anthony_o.swingifts.test.CreateSampleDbTest;
import com.github.anthony_o.swingifts.util.InjectUtils;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SessionServiceTest extends CreateSampleDbTest {

    @Test
    public void createWithPersonReturningToken5ActiveSessionsTest() throws Exception {
        SessionService sessionService = InjectUtils.getInstance(SessionService.class);

        byte[][] aliceTokens = new byte[6][];
        byte[][] bobTokens = new byte[6][];

        aliceTokens[0] = sessionService.createWithPersonIdReturningToken(alicePersonId);
        aliceTokens[1] = sessionService.createWithPersonIdReturningToken(alicePersonId);

        bobTokens[0] = sessionService.createWithPersonIdReturningToken(bobPersonId);
        bobTokens[1] = sessionService.createWithPersonIdReturningToken(bobPersonId);

        aliceTokens[2] = sessionService.createWithPersonIdReturningToken(alicePersonId);
        aliceTokens[3] = sessionService.createWithPersonIdReturningToken(alicePersonId);
        aliceTokens[4] = sessionService.createWithPersonIdReturningToken(alicePersonId);
        aliceTokens[5] = sessionService.createWithPersonIdReturningToken(alicePersonId);

        bobTokens[2] = sessionService.createWithPersonIdReturningToken(bobPersonId);
        bobTokens[3] = sessionService.createWithPersonIdReturningToken(bobPersonId);
        bobTokens[4] = sessionService.createWithPersonIdReturningToken(bobPersonId);
        bobTokens[5] = sessionService.createWithPersonIdReturningToken(bobPersonId);

        Thread.sleep(100); // Pause in order to correctly save the DB? The two following lines were failing without this.

        assertThat(sessionService.touchSession(alicePersonId, aliceTokens[0])).isFalse();
        assertThat(sessionService.touchSession(bobPersonId, bobTokens[0])).isFalse();

        assertThat(sessionService.touchSession(alicePersonId, aliceTokens[5])).isTrue();
        assertThat(sessionService.touchSession(bobPersonId, bobTokens[5])).isTrue();

        assertThat(sessionService.touchSession(alicePersonId, aliceTokens[1])).isTrue();
        assertThat(sessionService.touchSession(bobPersonId, bobTokens[1])).isTrue();
    }
}
