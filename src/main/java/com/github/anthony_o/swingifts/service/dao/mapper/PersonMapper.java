package com.github.anthony_o.swingifts.service.dao.mapper;

import com.github.anthony_o.swingifts.entity.Person;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class PersonMapper implements ResultSetMapper<Person> {

    @Override
    public Person map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        Person person = new Person();
        ResultSetMetaData metaData = r.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int i = 1 ; i <= columnCount; i++) { // iterate on column names thanks to http://stackoverflow.com/a/3599960/535203
            switch (metaData.getColumnLabel(i)) {
                case "ID":
                    person.setId(r.getObject(i, Long.class));
                    break;
                case "NAME":
                    person.setName(r.getString(i));
                    break;
                case "LOGIN":
                    person.setLogin(r.getString(i));
                    break;
                case "EMAIL":
                    person.setEmail(r.getString(i));
                    break;
                case "IS_USER":
                    person.setIsUser(r.getBoolean(i));
                    break;
                case "HAS_LOGIN":
                    person.setHasLogin(r.getBoolean(i));
                    break;
                case "HAS_EMAIL":
                    person.setHasEmail(r.getBoolean(i));
                    break;
                case "PASSWORD_HASH":
                    person.setPasswordHash(r.getBytes(i));
                    break;
                case "SALT":
                    person.setSalt(r.getBytes(i));
                    break;
            }
        }
        return person;
    }

}
