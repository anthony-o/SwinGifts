package com.github.anthony_o.swingifts.service.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public interface SessionDao {

    @SqlUpdate("insert into SESSION(PERSON_ID, TOKEN, LAST_USE) values (:personId, :token, CURRENT_TIMESTAMP())")
    @GetGeneratedKeys
    long createWithPersonIdAndToken(@Bind("personId") long personId, @Bind("token") byte[] token);

    @SqlUpdate("delete from SESSION where PERSON_ID = :personId and ID not in (select top 5 ID from SESSION where PERSON_ID = :personId order by LAST_USE desc)")
    void deleteOldWithPersonId(@Bind("personId") long personId);

    @SqlUpdate("update SESSION set LAST_USE = CURRENT_TIMESTAMP() where PERSON_ID = :personId and TOKEN = :token")
    int updateLastUseWithPersonIdAndToken(@Bind("personId") long personId, @Bind("token") byte[] token);

    @SqlUpdate("delete SESSION where PERSON_ID = :personId and TOKEN = :token")
    int deleteWithPersonIdAndToken(@Bind("personId") long personId, @Bind("token") byte[] token);
}
