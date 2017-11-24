package com.github.anthony_o.swingifts.service.dao;

import com.github.anthony_o.swingifts.entity.WishItem;
import com.github.anthony_o.swingifts.service.dao.mapper.WishItemMapper;
import com.github.anthony_o.swingifts.util.ServiceUtils;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(WishItemMapper.class)
public interface WishItemDao {
    String PUBLIC_PROJECTION = "ID, NAME, URL, PERSON_ID, CREATED_BY_PERSON_ID, CREATION_DATE, MODIFICATION_DATE";

    @SqlQuery("select " + PUBLIC_PROJECTION + " from WISH_ITEM where WISH_LIST_ID = :wishListId")
    List<WishItem> findWithWishListId(@Bind("wishListId") long wishListId);

    @SqlUpdate("insert into WISH_ITEM(WISH_LIST_ID, PERSON_ID, CREATED_BY_PERSON_ID, NAME, URL, CREATION_DATE) values (:wishItem.wishListId, :wishItem.personId, :wishItem.createdByPersonId, :wishItem.name, :wishItem.url, CURRENT_TIMESTAMP)")
    @GetGeneratedKeys
    long create(@BindBean("wishItem") WishItem wishItem);

    @SqlQuery("select " + PUBLIC_PROJECTION + " from WISH_ITEM where PERSON_ID = :personId")
    List<WishItem> findWithPersonId(@Bind("personId") long personId);

    @SqlQuery("select * from WISH_ITEM where ID = :id")
    WishItem findOne(@Bind("id") long id);

    @SqlQuery("select " + PUBLIC_PROJECTION + " from WISH_ITEM where ID = :id")
    WishItem findOneForUI(@Bind("id") long id);

    @SqlQuery("select case when wi.PERSON_ID is not null then wi.PERSON_ID else wl.PERSON_ID end from WISH_ITEM wi left join WISH_LIST wl on wi.WISH_LIST_ID = wl.ID where wi.ID = :id")
    Long findOnePersonIdSearchingUpToWishListIfNull(@Bind("id") long id);

    @SqlUpdate("update WISH_ITEM set NAME = :wishItem.name, URL = :wishItem.url, MODIFICATION_DATE = CURRENT_TIMESTAMP where ID = :wishItem.id")
    int update(@BindBean("wishItem") WishItem wishItem);

    @SqlUpdate("delete WISH_ITEM where ID = :id")
    int delete(@Bind("id") long id);

    default int deleteWithReservations(@Bind("id") long id) throws Exception {
        return ServiceUtils.inTransaction(() -> {
            ServiceUtils.attachIfTransactionElseOnDemand(ReservationDao.class).deleteWithWishItemId(id);

            return delete(id);
        });
    }

    @SqlQuery("select count(1) from WISH_ITEM wi left join WISH_LIST wl on wi.WISH_LIST_ID = wl.ID left join WISH_LIST all_wl on wl.EVENT_ID = all_wl.EVENT_ID" +
            " where wi.ID = :id and (wi.PERSON_ID = :askerPersonId or (all_wl.PERSON_ID = :askerPersonId and wl.PERSON_ID <> :askerPersonId))")
    int countAskerIsInEventAndDoesntOwnTheWishItemSWishListOrOwnsWishItemWithIdAndAskerPersonId(@Bind("id") long id, @Bind("askerPersonId") long askerPersonId);

    @SqlQuery("select count(1) from WISH_ITEM wi left join WISH_LIST wl on wi.WISH_LIST_ID = wl.ID left join WISH_LIST event_wls on wl.EVENT_ID = event_wls.EVENT_ID" +
            " where wi.ID = :id and" +
                " (wi.PERSON_ID = :askerPersonId or event_wls.PERSON_ID = :askerPersonId" +
                    " or exists(select 1 from WISH_LIST asker_wls, WISH_LIST owner_wls where asker_wls.PERSON_ID = :askerPersonId and owner_wls.PERSON_ID = wi.PERSON_ID and asker_wls.EVENT_ID = owner_wls.EVENT_ID))") // check if the asker shares an event with the wishItem owner
    int countAskerIsInEventOrOwnsWishItemOrShareEventWithWishItemOwnerWithIdAndAskerPersonId(@Bind("id") long id, @Bind("askerPersonId") long askerPersonId);

    @SqlQuery("select count(1) from WISH_ITEM wi left join WISH_LIST wl on wi.WISH_LIST_ID = wl.ID left join WISH_LIST event_wls on wl.EVENT_ID = event_wls.EVENT_ID" +
            " where wi.ID = :id and" +
                " (event_wls.PERSON_ID = :askerPersonId" +
                    " or exists(select 1 from WISH_LIST asker_wls, WISH_LIST owner_wls where asker_wls.PERSON_ID = :askerPersonId and owner_wls.PERSON_ID = wi.PERSON_ID and asker_wls.EVENT_ID = owner_wls.EVENT_ID))") // check if the asker shares an event with the wishItem owner
    int countAskerIsInEventOrShareEventWithWishItemOwnerWithIdAndAskerPersonId(@Bind("id") long id, @Bind("askerPersonId") long askerPersonId);

    @SqlUpdate("delete WISH_ITEM where WISH_LIST_ID = :wishListId")
    int deleteWithWishListId(@Bind("wishListId") long wishListId);
}
