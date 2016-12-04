package com.github.anthony_o.swingifts.service.dao;

import com.github.anthony_o.swingifts.entity.Event;
import com.github.anthony_o.swingifts.service.dao.mapper.EventMapper;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(EventMapper.class)
public interface EventDao {
    @SqlQuery("select ID, NAME, IS_OPENED from EVENT where ID = :id and KEY = :key")
    Event findOneWithIdAndKey(@Bind("id") long id, @Bind("key") byte[] key);

    @SqlQuery("select count(1) from EVENT where ID = :id and KEY = :key and IS_OPENED = true")
    int countOpenedWithIdAndKey(@Bind("id") long id, @Bind("key") byte[] key);

    @SqlQuery("select ID, NAME, IS_OPENED, IS_CIRCLE_GIFT_LAUNCHED_ONCE from EVENT where ID = :id")
    Event findOneWithId(@Bind("id") long id);

    @SqlQuery("select e.ID, e.NAME, e.KEY, e.IS_OPENED from EVENT e join WISH_LIST w on e.ID = w.EVENT_ID where w.PERSON_ID = :askerPersonId")
    List<Event> findWithAskerPersonId(@Bind("askerPersonId") long askerPersonId);

    @SqlUpdate("insert into EVENT(NAME, KEY) values (:event.name, :key)")
    @GetGeneratedKeys
    long createWithEventAndKey(@BindBean("event") Event event, @Bind("key") byte[] key);

    @SqlUpdate("update EVENT set IS_CIRCLE_GIFT_LAUNCHED_ONCE = :isCircleGiftLaunchedOnce where ID = :id")
    int updateIsCircleGiftLaunchedOnceWithIdAndCircleGiftLaunchedOnce(@Bind("id") long id, @Bind("isCircleGiftLaunchedOnce") boolean isCircleGiftLaunchedOnce);
}
