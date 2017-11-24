package com.github.anthony_o.swingifts.service.dao;

import com.github.anthony_o.swingifts.entity.Reservation;
import com.github.anthony_o.swingifts.service.dao.mapper.ReservationMapper;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(ReservationMapper.class)
public interface ReservationDao {

    @SqlUpdate("insert into RESERVATION(PERSON_ID, WISH_ITEM_ID, CREATION_DATE) values (:reservation.personId, :reservation.wishItemId, CURRENT_TIMESTAMP)")
    @GetGeneratedKeys
    long create(@BindBean("reservation") Reservation reservation);

    @SqlQuery("select WISH_ITEM_ID from RESERVATION where ID = :id")
    Long findOneWishItemId(@Bind("id") long id);

    @SqlUpdate("delete from RESERVATION where ID = :id")
    int delete(@Bind("id") long id);

    @SqlQuery("select ID, PERSON_ID, CREATION_DATE from RESERVATION where WISH_ITEM_ID = :wishItemId")
    List<Reservation> findWithWishItemId(@Bind("wishItemId") long wishItemId);

    @SqlUpdate("delete RESERVATION where WISH_ITEM_ID = :wishItemId")
    int deleteWithWishItemId(@Bind("wishItemId") long wishItemId);
}
