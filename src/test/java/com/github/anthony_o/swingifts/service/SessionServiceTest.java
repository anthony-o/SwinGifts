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

        aliceTokens[0] = createWithPersonIdReturningTokenAndMicroSleep(sessionService, alicePersonId);
        aliceTokens[1] = createWithPersonIdReturningTokenAndMicroSleep(sessionService, alicePersonId);

        bobTokens[0] = createWithPersonIdReturningTokenAndMicroSleep(sessionService, bobPersonId);
        bobTokens[1] = createWithPersonIdReturningTokenAndMicroSleep(sessionService, bobPersonId);

        aliceTokens[2] = createWithPersonIdReturningTokenAndMicroSleep(sessionService, alicePersonId);
        aliceTokens[3] = createWithPersonIdReturningTokenAndMicroSleep(sessionService, alicePersonId);
        aliceTokens[4] = createWithPersonIdReturningTokenAndMicroSleep(sessionService, alicePersonId);
        aliceTokens[5] = createWithPersonIdReturningTokenAndMicroSleep(sessionService, alicePersonId);

        bobTokens[2] = createWithPersonIdReturningTokenAndMicroSleep(sessionService, bobPersonId);
        bobTokens[3] = createWithPersonIdReturningTokenAndMicroSleep(sessionService, bobPersonId);
        bobTokens[4] = createWithPersonIdReturningTokenAndMicroSleep(sessionService, bobPersonId);
        bobTokens[5] = createWithPersonIdReturningTokenAndMicroSleep(sessionService, bobPersonId);

        assertThat(sessionService.touchSession(alicePersonId, aliceTokens[0])).isFalse();
        assertThat(sessionService.touchSession(bobPersonId, bobTokens[0])).isFalse();

        assertThat(sessionService.touchSession(alicePersonId, aliceTokens[5])).isTrue();
        assertThat(sessionService.touchSession(bobPersonId, bobTokens[5])).isTrue();

        assertThat(sessionService.touchSession(alicePersonId, aliceTokens[1])).isTrue();
        assertThat(sessionService.touchSession(bobPersonId, bobTokens[1])).isTrue();
    }

    private byte[] createWithPersonIdReturningTokenAndMicroSleep(SessionService sessionService, long personId) throws Exception {
        byte[] token = sessionService.createWithPersonIdReturningToken(personId);
        Thread.sleep(10); // tests were not always successful without that small pause //TODO inspect why
        return token;
    }
}
