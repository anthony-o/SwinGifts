package com.github.anthony_o.swingifts.api;

import com.github.anthony_o.swingifts.entity.Event;
import com.github.anthony_o.swingifts.test.CreateSampleDbTest;
import com.github.anthony_o.swingifts.util.SessionUtils;
import org.junit.Test;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;

import static org.assertj.core.api.Assertions.*;

public class EventResourceTest extends CreateSampleDbTest {

    @Test()
    public void findOneWithIdAndKeyWhenNotLoggedInTest() {
        EventResource eventResource = new EventResource();
        Event event = eventResource.findOneWithIdAndKey(bobSEventId, bobSEventKeyBase64RFC4648);
        assertThat(event.getId()).isEqualTo(bobSEventId);
        assertThat(event.getIsOpened()).isFalse();
    }

    @Test(expected = NotAuthorizedException.class)
    public void findOneWithIdAndKeyWhenNotLoggedInAndWithoutKeyTest() {
        EventResource eventResource = new EventResource();
        eventResource.findOneWithIdAndKey(bobSEventId, null);
    }

    @Test(expected = ForbiddenException.class)
    public void findOneWithIdAndKeyWhenNotInEventAndWithoutKeyTest() {
        EventResource eventResource = new EventResource();
        SessionUtils.getOrCreateSession().setPersonId(charliePersonId);
        eventResource.findOneWithIdAndKey(bobSEventId, null);
    }

    @Test()
    public void findOneWithIdAndKeyTest() {
        EventResource eventResource = new EventResource();
        SessionUtils.getOrCreateSession().setPersonId(alicePersonId);
        eventResource.findOneWithIdAndKey(bobSEventId, bobSEventKeyBase64RFC4648);
    }

}
