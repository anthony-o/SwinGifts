package com.github.anthony_o.swingifts.service.dao;

import com.github.anthony_o.swingifts.entity.WishList;
import com.github.anthony_o.swingifts.service.dao.mapper.WishListMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.GetHandle;

import java.util.List;


@RegisterMapper(WishListMapper.class)
public interface WishListDao extends GetHandle {

    String PUBLIC_PROJECTION_USING_WL_ALIAS = "wl.ID, wl.PERSON_ID, wl.EVENT_ID, wl.IS_PERSON_PARTICIPATES_IN_CIRCLE_GIFT";

    String PUBLIC_PROJECTION = "ID, PERSON_ID, EVENT_ID, IS_PERSON_PARTICIPATES_IN_CIRCLE_GIFT";
    String PRIVATE_PROJECTION = PUBLIC_PROJECTION + ", IS_PERSON_EVENT_ADMIN, CIRCLE_GIFT_TARGET_PERSON_ID, IS_CIRCLE_GIFT_TARGET_PERSON_ID_READ";

    @SqlUpdate("insert into WISH_LIST(EVENT_ID, PERSON_ID) values (:eventId, :personId)")
    @GetGeneratedKeys
    long createWithEventIdAndPersonId(@Bind("eventId") long eventId, @Bind("personId") long personId);

    @SqlUpdate("insert into WISH_LIST(EVENT_ID, PERSON_ID, IS_PERSON_EVENT_ADMIN) values (:eventId, :personId, true)")
    @GetGeneratedKeys
    long createAdminWithEventIdAndPersonId(@Bind("eventId") long eventId, @Bind("personId") long personId);

    @SqlQuery("select count(1) from WISH_LIST where EVENT_ID = :eventId and PERSON_ID = :personId")
    int countWithEventIdAndPersonId(@Bind("eventId") long eventId, @Bind("personId") long personId);

    @SqlQuery("select count(1) from WISH_LIST where ID = :id and PERSON_ID = :personId")
    int countWithIdAndPersonId(@Bind("id") long id, @Bind("personId") long personId);

    /*
    @SqlQuery("select count(1) from WISH_ITEM wi left join WISH_LIST wil on wi.WISH_LIST_ID = wil.ID where wi.ID = :wishItemId and (" +
            " exists (select 1 from WISH_LIST all_wl where all_wl.PERSON_ID = :askerPersonId and all_wl.EVENT_ID = wil.EVENT_ID)" + // the wishitem is linked to a wishlist and the person is part of another wishlist which is in the same event
            " or exists (select 1 from WISH_LIST all_wl_of_wi_person join WISH_LIST all_wl_for_same_events on all_wl_of_wi_person.EVENT_ID = all_wl_for_same_events.EVENT_ID where all_wl_of_wi_person.PERSON_ID = wi.PERSON_ID and all_wl_for_same_events.PERSON_ID = :askerPersonId)" + // from all the events from which the person linked to the wishItem directly is taking part of, check that there is a wishlist that matches the given personId
            ")")
    int countWithWishItemIdAndAskerPersonId(@Bind("wishItemId") long wishItemId, @Bind("askerPersonId") long askerPersonId);
    */

    @SqlQuery("select count(1) from WISH_LIST w, WISH_LIST all_w_in_event where w.ID = :id and all_w_in_event.EVENT_ID = w.EVENT_ID and all_w_in_event.PERSON_ID = :askerPersonId")
    int countWithIdAndAskerPersonId(@Bind("id") long id, @Bind("askerPersonId") long askerPersonId);

    @SqlQuery("select count(1) from WISH_LIST where EVENT_ID = :eventId and PERSON_ID = :personId and IS_PERSON_EVENT_ADMIN = true")
    int countAdminWithEventIdAndPersonId(@Bind("eventId") long eventId, @Bind("personId") long personId);

    @SqlQuery("select " + PRIVATE_PROJECTION + " from WISH_LIST where EVENT_ID = :eventId and PERSON_ID = :personId")
    WishList findOneWithEventIdAndPersonIdUsingPrivateProjection(@Bind("eventId") long eventId, @Bind("personId") long personId);

    @SqlQuery("select " + PUBLIC_PROJECTION + " from WISH_LIST where EVENT_ID = :eventId and PERSON_ID = :personId")
    WishList findOneWithEventIdAndPersonIdUsingPublicProjection(@Bind("eventId") long eventId, @Bind("personId") long personId);

    @SqlQuery("select PERSON_ID from WISH_LIST where IS_PERSON_PARTICIPATES_IN_CIRCLE_GIFT = true and EVENT_ID = :eventId")
    List<Long> findPersonsParticipatingInCircleGiftWithEventIdReturningPersonId(@Bind("eventId") long eventId);

    @SqlUpdate("update WISH_LIST set CIRCLE_GIFT_TARGET_PERSON_ID = :circleGiftTargetPersonId where EVENT_ID = :eventId and PERSON_ID = :personId")
    int updateCircleGiftTargetPersonIdWithEventIdAndPersonIdAndCircleGiftTargetPersonId(@Bind("eventId") long eventId, @Bind("personId") long personId, @Bind("circleGiftTargetPersonId") long circleGiftTargetPersonId);

    @SqlQuery("select * from WISH_LIST where EVENT_ID = :eventId")
    List<WishList> findWithEventId(@Bind("eventId") long eventId);

    @SqlUpdate("update WISH_LIST set IS_CIRCLE_GIFT_TARGET_PERSON_ID_READ = :isCircleGiftTargetPersonIdRead where ID = :id")
    int updateIsCircleGiftTargetPersonIdReadWithIdAndIsCircleGiftTargetPersonIdRead(@Bind("id") long id, @Bind("isCircleGiftTargetPersonIdRead") boolean isCircleGiftTargetPersonIdRead);

    default int update(WishList wishList) {
        StringBuilder updateQuery = new StringBuilder("update WISH_LIST set");
        if (wishList.getIsPersonParticipatesInCircleGift() != null) {
            updateQuery.append(" IS_PERSON_PARTICIPATES_IN_CIRCLE_GIFT = :isPersonParticipatesInCircleGift");
        }
        updateQuery.append(" where ID = :id");
        return withHandle((handle) -> handle
            .createStatement(updateQuery.toString())
            .bindFromProperties(wishList)
            .execute());
    }

    @SqlQuery("select * from WISH_LIST where ID = :id")
    WishList findOne(@Bind("id") long id);

    @SqlQuery("select " + PUBLIC_PROJECTION_USING_WL_ALIAS + ", case when wl.PERSON_ID <> :askerPersonId then (select count(1) from WISH_ITEM where WISH_LIST_ID = wl.ID) else 0 end + (select count(1) from WISH_ITEM where PERSON_ID = wl.PERSON_ID) as WISH_ITEMS_COUNT" +
            " from WISH_LIST wl join PERSON p on wl.PERSON_ID = p.ID" +
            " where EVENT_ID = :eventId" +
            " order by p.NAME")
    List<WishList> findWithEventIdAndAskerPersonIdCountingWishItemsOrderByPersonName(@Bind("eventId") long eventId, @Bind("askerPersonId") long askerPersonId);

    @SqlUpdate("delete WISH_LIST where ID = :id")
    int delete(@Bind("id") long id);
}
