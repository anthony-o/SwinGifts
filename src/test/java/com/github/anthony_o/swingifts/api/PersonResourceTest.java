package com.github.anthony_o.swingifts.api;

import com.github.anthony_o.swingifts.entity.Person;
import com.github.anthony_o.swingifts.service.dao.PersonDao;
import com.github.anthony_o.swingifts.test.CreateSampleDbTest;
import com.github.anthony_o.swingifts.util.ServiceUtils;
import com.github.anthony_o.swingifts.util.SessionUtils;
import org.junit.Test;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Response;
import static org.assertj.core.api.Assertions.*;

public class PersonResourceTest extends CreateSampleDbTest {

    @Test
    public void createWithPersonAndEventIdAndEventKeyThenCreateSessionTest() throws Exception {
        PersonResource personResource = new PersonResource();
        Person person = new Person();
        person.setName("Test");
        person.setPassword("TestPwd");
        Response response = personResource.createWithPersonAndEventIdAndEventKeyThenCreateSession(person, firstEventId, firstEventKeyBase64RFC4648, true);
        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
        assertThat(response.getCookies()).containsKey(SessionUtils.SESSION_COOKIE_NAME);
    }

    @Test(expected = ForbiddenException.class)
    public void createWithPersonAndEventIdAndEventKeyThenCreateSessionOnClosedEventTest() throws Exception {
        PersonResource personResource = new PersonResource();
        Person person = new Person();
        person.setName("Test");
        person.setPassword("TestPwd");
        personResource.createWithPersonAndEventIdAndEventKeyThenCreateSession(person, bobSEventId, bobSEventKeyBase64RFC4648, true);
    }

    @Test(expected = ForbiddenException.class)
    public void createWithPersonAndEventIdAndEventKeyThenCreateSessionOnClosedEventWithoutKeyWhenPersonInEventButNotAdminTest() throws Exception {
        PersonResource personResource = new PersonResource();
        Person person = new Person();
        person.setName("Test");
        person.setPassword("TestPwd");
        SessionUtils.getOrCreateSession().setPersonId(alicePersonId);
        personResource.createWithPersonAndEventIdAndEventKeyThenCreateSession(person, bobSEventId, null, false);
    }

    @Test
    public void updateWithPersonAndPersonIdThenCreateSessionForPersonInClosedEventTest() throws Exception {
        PersonResource personResource = new PersonResource();
        Person person = new Person();
        person.setId(alicePersonId);
        person.setPassword("alice");
        Response response = personResource.updateWithPersonAndPersonIdThenCreateSession(person, bobSEventId, true);
        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
        assertThat(response.getCookies()).containsKey(SessionUtils.SESSION_COOKIE_NAME);
    }

    @Test
    public void createWithPersonAndEventIdAndEventKeyThenCreateSessionWithoutEvent() throws Exception {
        PersonResource personResource = new PersonResource();
        Person person = new Person();
        person.setName("Test");
        person.setLogin("test");
        person.setPassword("test");
        Response response = personResource.createWithPersonAndEventIdAndEventKeyThenCreateSession(person, null, null, true);
        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
        assertThat(response.getCookies()).containsKey(SessionUtils.SESSION_COOKIE_NAME);
        Person personInDb = ServiceUtils.attachIfTransactionElseOnDemand(PersonDao.class).findOneWithLogin("test");
        assertThat(personInDb.getName()).isEqualTo("Test");
    }

    /*
    @Test(expected = ForbiddenException.class)
    public void updateWithPersonAndPersonIdThenCreateSessionForPersonNotInClosedEventTest() throws Exception {
        SessionUtils.getOrCreateSession().setPersonId(charliePersonId);
        PersonResource personResource = new PersonResource();
        Person person = new Person();
        person.setId(charliePersonId);
        person.setPassword("charlie");
        personResource.updateWithPersonAndPersonIdThenCreateSession(person, bobSEventId, true);
    }
    */

}
