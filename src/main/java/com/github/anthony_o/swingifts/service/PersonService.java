package com.github.anthony_o.swingifts.service;

import com.github.anthony_o.swingifts.entity.Person;
import com.github.anthony_o.swingifts.entity.PersonWithWishList;
import com.github.anthony_o.swingifts.service.dao.EventDao;
import com.github.anthony_o.swingifts.service.dao.PersonDao;
import com.github.anthony_o.swingifts.service.dao.WishListDao;
import com.github.anthony_o.swingifts.util.ServiceUtils;
import com.google.inject.Singleton;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;

@Singleton
public class PersonService {
    public static final int SALT_LENGTH = 66;
    public static final int PASSWORD_ITERATIONS = 1000;
    public static final int PASSWORD_LENGTH = 66;

    private PersonDao getPersonDao() {
        return ServiceUtils.attachIfTransactionElseOnDemand(PersonDao.class);
    }

    private WishListDao getWishListDao() {
        return ServiceUtils.attachIfTransactionElseOnDemand(WishListDao.class);
    }

    private EventDao getEventDao() {
        return ServiceUtils.attachIfTransactionElseOnDemand(EventDao.class);
    }

    public List<Person> findWithEventIdAndEventKeyOrderByName(Long eventId, byte[] eventKey) {
        return getPersonDao().findWithEventIdAndEventKeyOrderByName(eventId, eventKey);
    }

    public PersonWithWishList createWithPersonThenCreateWishListWithEventIdAndEventKey(Person person, Long eventId, byte[] eventKey) throws Exception {
        return ServiceUtils.inTransaction(() -> {
            PersonWithWishList personWithWishList = new PersonWithWishList();
            // Must first create the person and then create its wishList
            long id = createWithPerson(person);
            personWithWishList.setId(id);

            if (getEventDao().countOpenedWithIdAndKey(eventId, eventKey) != 1) {
                throw new ForbiddenException("This eventKey and eventId don't match or the event is not opened.");
            }

            personWithWishList.setWishListId(getWishListDao().createWithEventIdAndPersonId(eventId, id));

            return personWithWishList;
        });
    }

    public Person findWithIdAndSessionToken(long id, byte[] sessionToken) {
        return getPersonDao().findWithIdAndSessionToken(id, sessionToken);
    }

    public List<Person> findWithEventIdAndAskerPersonIdOrderByName(long eventId, long personId) {
        List<Person> persons = getPersonDao().findWithEventIdOrderByName(eventId);
        // only a person in this list has the right to use this method, let's check this
        boolean found = false;
        for (Person person : persons) {
            if (person.getId() == personId) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new ForbiddenException("You are not part of this event.");
        }
        return persons;
    }

    public PersonWithWishList createWithPersonThenCreateWishListWithEventIdAndAskerPersonId(Person person, long eventId, long askerPersonId) throws Exception {
        return ServiceUtils.inTransaction(() -> {
            WishListDao wishListDao = getWishListDao();

            PersonWithWishList personWithWishList = new PersonWithWishList();

            // assert that the asker is in the event
            ServiceUtils.checkThatAskerIsAdminWithEventIdAndAskerPersonId(eventId, askerPersonId, wishListDao);
            long id = createWithPerson(person);
            personWithWishList.setId(id);

            personWithWishList.setWishListId(wishListDao.createWithEventIdAndPersonId(eventId, id));

            return personWithWishList;
        });
    }

    public long createWithPerson(Person person) throws InvalidKeySpecException, NoSuchAlgorithmException {
        // Must first create the person and then create its wishList
        byte[] salt = new byte[SALT_LENGTH];
        byte[] passwordHash = null;

        String password = person.getPassword();
        long personId;
        if (StringUtils.isNotBlank(password)) {
            passwordHash = createSaltThenComputePasswordHash(salt, password);
            personId = getPersonDao().createWithPersonAndPasswordHashAndSalt(person, passwordHash, salt);
        } else if (password == null){
            personId = getPersonDao().createWithPerson(person);
        } else {
            throw new BadRequestException("Password must be set");
        }
        person.setId(personId);
        return personId;
    }

    protected static byte[] createSaltThenComputePasswordHash(byte[] salt, String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] passwordHash;
        // computing password hash thanks to:
        // - http://security.stackexchange.com/a/4801/18432
        // - http://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
        // - http://stackoverflow.com/a/27928435/535203
        new SecureRandom().nextBytes(salt); // not using getInstanceStrong() because it was too long and useless according to https://tersesystems.com/2015/12/17/the-right-way-to-use-securerandom/

        passwordHash = computePasswordHash(password, salt);
        return passwordHash;
    }

    protected static byte[] computePasswordHash(String password, byte[] salt) throws InvalidKeySpecException, NoSuchAlgorithmException {
        //TODO check password strength
        byte[] passwordHash;
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, PASSWORD_ITERATIONS, PASSWORD_LENGTH * 8);
        passwordHash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(keySpec).getEncoded();
        return passwordHash;
    }

    public boolean checkOrCreatePasswordThenUpdateWithPerson(Person person) throws Exception {
        return ServiceUtils.inTransaction(() -> {

            PersonDao personDao = getPersonDao();

            Long id = person.getId();
            Person personInDb = personDao.findOnePasswordHashAndSaltWithId(id);
            String password = person.getPassword();
            byte[] passwordHash = personInDb.getPasswordHash();
            byte[] salt = personInDb.getSalt();

            if (StringUtils.isNotBlank(password)) {
                if (passwordHash == null && salt == null) {
                    // We are creating a new password
                    salt = new byte[SALT_LENGTH];
                    passwordHash = createSaltThenComputePasswordHash(salt, password);
                    personDao.updateWithPasswordHashAndSalt(id, passwordHash, salt);
                } else {
                    // check that the password matches
                    byte[] passwordHashInDb = computePasswordHash(password, salt);
                    if (!Arrays.equals(passwordHash, passwordHashInDb)) {
                        throw new BadRequestException("Wrong password");
                    }
                }

                String name = person.getName();
                if (name != null) {
                    personDao.updateWithName(id, name);
                }
                if (BooleanUtils.isFalse(person.getHasLogin())) {
                    personDao.updateWithLogin(id, person.getLogin());
                }
                if (BooleanUtils.isFalse(person.getHasEmail())) {
                    personDao.updateWithEmail(id, person.getEmail());
                }
            } else {
                throw new BadRequestException("Password must be set");
            }

            return true;
        });
    }

    public Long findOneIdWithLogin(String login) {
        return getPersonDao().findOneIdWithLogin(login);
    }

    public Person findOneWithId(long personId) {
        return getPersonDao().findOneUsingPrivateProjection(personId);
    }

    public boolean resetPasswordWithIdAndEventIdAndAskerPersonId(long id, long eventId, long askerPersonId) {
        PersonDao personDao = getPersonDao();
        ServiceUtils.checkThatAskerIsAdminWithEventIdAndAskerPersonId(eventId, askerPersonId, getWishListDao());
        Person person = personDao.findOneUsingPublicProjection(id);
        if (BooleanUtils.isTrue(person.getIsUser()) && BooleanUtils.isFalse(person.getHasEmail())) {
            if (personDao.resetPasswordAndSaltWithIdIfIsUserAndNotHaveEmail(id) == 1) {
                return true;
            } else {
                throw new IllegalStateException("Problem while resetting the password");
            }
        } else {
            return false;
        }
    }
}
