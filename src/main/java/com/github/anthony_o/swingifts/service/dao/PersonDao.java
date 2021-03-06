package com.github.anthony_o.swingifts.service.dao;

import com.github.anthony_o.swingifts.entity.Person;
import com.github.anthony_o.swingifts.service.dao.mapper.PersonMapper;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(PersonMapper.class)
public interface PersonDao {

    String PUBLIC_PROJECTION = "p.NAME, p.ID, p.PASSWORD_HASH is not null as IS_USER, p.LOGIN is not null as HAS_LOGIN, p.EMAIL is not null as HAS_EMAIL";
    String AUTHENTICATED_PROJECTION = PUBLIC_PROJECTION + ", p.LOGIN";
    String PRIVATE_PROJECTION = AUTHENTICATED_PROJECTION + ", p.EMAIL";

    @SqlQuery("select " + PUBLIC_PROJECTION + " from WISH_LIST w INNER JOIN PERSON p ON w.PERSON_ID = p.ID INNER JOIN EVENT e ON w.EVENT_ID = e.ID where w.EVENT_ID = :eventId and e.KEY = :eventKey order by p.NAME")
    List<Person> findWithEventIdAndEventKeyOrderByName(@Bind("eventId") long eventId, @Bind("eventKey") byte[] eventKey);

    @SqlUpdate("insert into PERSON(NAME, EMAIL, LOGIN, PASSWORD_HASH, SALT, CREATION_DATE) values (:person.name, :person.email, :person.login, :passwordHash, :salt, CURRENT_TIMESTAMP)")
    @GetGeneratedKeys
    long createWithPersonAndPasswordHashAndSalt(@BindBean("person") Person person, @Bind("passwordHash") byte[] passwordHash, @Bind("salt") byte[] salt);

    @SqlQuery("select " + PRIVATE_PROJECTION + " from PERSON p INNER JOIN SESSION s ON p.ID = s.PERSON_ID where p.ID = :id and s.token = :sessionToken")
    Person findWithIdAndSessionToken(@Bind("id") long id, @Bind("sessionToken") byte[] sessionToken);

    @SqlQuery("select " + PUBLIC_PROJECTION + " from WISH_LIST w INNER JOIN PERSON p ON w.PERSON_ID = p.ID INNER JOIN EVENT e ON w.EVENT_ID = e.ID where w.EVENT_ID = :eventId order by p.NAME")
    List<Person> findWithEventIdOrderByName(@Bind("eventId") long eventId);

    @SqlQuery("select PASSWORD_HASH, SALT from PERSON where ID = :id")
    Person findOnePasswordHashAndSaltWithId(@Bind("id") long id);

    @SqlUpdate("update PERSON set NAME = :name, MODIFICATION_DATE = CURRENT_TIMESTAMP where ID = :id")
    int updateWithName(@Bind("id") long id, @Bind("name") String name);

    @SqlUpdate("update PERSON set LOGIN = :login, MODIFICATION_DATE = CURRENT_TIMESTAMP where ID = :id")
    int updateWithLogin(@Bind("id") long id, @Bind("login") String login);

    @SqlUpdate("update PERSON set PASSWORD_HASH = :passwordHash, SALT = :salt, MODIFICATION_DATE = CURRENT_TIMESTAMP where ID = :id")
    int updateWithPasswordHashAndSalt(@Bind("id") long id, @Bind("passwordHash") byte[] passwordHash, @Bind("salt") byte[] salt);

    @SqlQuery("select ID from PERSON where login = :login")
    Long findOneIdWithLogin(@Bind("login") String login);

    @SqlQuery("select " + PRIVATE_PROJECTION + " from PERSON p where p.login = :login")
    Person findOneWithLogin(@Bind("login") String login);

    @SqlUpdate("update PERSON set EMAIL = :email, MODIFICATION_DATE = CURRENT_TIMESTAMP where ID = :id")
    int updateWithEmail(@Bind("id") long id, @Bind("email") String email);

    @SqlQuery("select * from PERSON where ID = :id")
    Person findOne(@Bind("id") long id);

    @SqlQuery("select " + PRIVATE_PROJECTION + " from PERSON p where p.ID = :id")
    Person findOneUsingPrivateProjection(@Bind("id") long id);

    @SqlQuery("select " + PUBLIC_PROJECTION + " from PERSON p where p.ID = :id")
    Person findOneUsingPublicProjection(@Bind("id") long id);

    @SqlUpdate("insert into PERSON(NAME, EMAIL, LOGIN, CREATION_DATE) values (:person.name, :person.email, :person.login, CURRENT_TIMESTAMP)")
    @GetGeneratedKeys
    long createWithPerson(@BindBean("person") Person person);

    @SqlUpdate("delete PERSON where ID = :id and PASSWORD_HASH is null")
    int deleteIfIsNotUser(@Bind("id") long id);

    @SqlUpdate("update PERSON set PASSWORD_HASH = null, SALT = null, MODIFICATION_DATE = CURRENT_TIMESTAMP where ID = :id and PASSWORD_HASH is not null and EMAIL is null")
    int resetPasswordAndSaltWithIdIfIsUserAndNotHaveEmail(@Bind("id") long id);

    @SqlQuery("select distinct " + AUTHENTICATED_PROJECTION + " from WISH_LIST w INNER JOIN PERSON p ON w.PERSON_ID = p.ID where w.EVENT_ID IN (select EVENT_ID from WISH_LIST where PERSON_ID = :askerId) order by p.NAME")
    List<Person> findSharingEventsWithAskerIdOrderByName(@Bind("askerId") long askerId);
}
