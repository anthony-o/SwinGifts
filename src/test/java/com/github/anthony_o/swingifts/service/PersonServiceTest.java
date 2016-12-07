package com.github.anthony_o.swingifts.service;

import com.github.anthony_o.swingifts.entity.Person;
import com.github.anthony_o.swingifts.entity.PersonWithWishList;
import com.github.anthony_o.swingifts.service.dao.PersonDao;
import com.github.anthony_o.swingifts.test.CreateSampleDbTest;
import com.github.anthony_o.swingifts.util.InjectUtils;
import com.github.anthony_o.swingifts.util.ServiceUtils;
import org.junit.Test;

import javax.ws.rs.BadRequestException;
import javax.xml.bind.DatatypeConverter;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class PersonServiceTest extends CreateSampleDbTest {

    private PersonService getPersonService() {
        return InjectUtils.getInstance(PersonService.class);
    }

    private SessionService getSessionService() {
        return InjectUtils.getInstance(SessionService.class);
    }

    @Test
    public void computePasswordHashTest() throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] salt = new byte[PersonService.SALT_LENGTH];
        Arrays.fill(salt, (byte)0);
        assertThat(new PersonService().computePasswordHash("alice", salt)).inHexadecimal().isEqualTo(DatatypeConverter.parseHexBinary("C053670FA6996F43F50BF9B4CDED00ABBEA35DE6145C46561DA9682BA8BC735563FDB758FD787F6C66B80C00E39AB6192EE941D3DF9B8B4FB341BB13CF59CF9EC27F"));
    }

    @Test
    public void checkOrCreatePasswordThenUpdateWithPersonTest() throws Exception {
        PersonService personService = getPersonService();
        Person person = new Person();
        person.setId(alicePersonId);
        person.setPassword("alice");
        person.setHasEmail(false);
        person.setEmail("alice@wonder.land");
        personService.checkOrCreatePasswordThenUpdateWithPerson(person);
        Person alice = ServiceUtils.attachIfTransactionElseOnDemand(PersonDao.class).findOne(alicePersonId);
        assertThat(alice.getEmail()).isEqualTo("alice@wonder.land");
    }

    @Test(expected = BadRequestException.class)
    public void checkOrCreatePasswordThenUpdateWithPersonUsingWrongPasswordTest() throws Exception {
        PersonService personService = getPersonService();
        Person person = new Person();
        person.setId(alicePersonId);
        person.setPassword("wrong password");
        personService.checkOrCreatePasswordThenUpdateWithPerson(person);
    }

    @Test
    public void findWithEventIdAndEventKeyOrderByNameTest() {
        List<Person> bobSEventPersons = getPersonService().findWithEventIdAndEventKeyOrderByName(bobSEventId, bobSEventKey);
        assertThat(bobSEventPersons).hasSize(numberOfPersonsInBobSEvent);
        Person alice = bobSEventPersons.get(0);
        assertThat(alice.getName()).isEqualTo("Alice");
        assertThat(alice.getHasLogin()).isTrue();
        assertThat(alice.getHasEmail()).isFalse();
    }

    @Test
    public void createWithPersonThenCreateWishListWithEventIdAndAskerPersonIdTest() throws Exception {
        Person person = new Person();
        person.setName("Test");
        PersonWithWishList personWithWishList = getPersonService().createWithPersonThenCreateWishListWithEventIdAndAskerPersonId(person, firstEventId, alicePersonId);
        assertThat(personWithWishList.getId()).isNotNull();
        assertThat(personWithWishList.getWishListId()).isNotNull();
    }

    @Test
    public void findWithIdAndSessionTokenTest() throws Exception {
        Person alice = new Person();
        alice.setId(alicePersonId);
        byte[] token = getSessionService().createWithPersonReturningToken(alice);
        Person foundAlice = getPersonService().findWithIdAndSessionToken(alicePersonId, token);
        assertThat(foundAlice.getName()).isEqualTo("Alice");
        assertThat(foundAlice.getLogin()).isEqualTo("alice");
    }

    @Test
    public void findOneWithIdTest() {
        Person alice = getPersonService().findOneWithId(alicePersonId);
        assertThat(alice.getLogin()).isEqualTo("alice");
        assertThat(alice.getPasswordHash()).isNull();
    }
}
