package com.github.anthony_o.swingifts.service;

import com.github.anthony_o.swingifts.entity.Person;
import com.github.anthony_o.swingifts.entity.WishItem;
import com.github.anthony_o.swingifts.entity.WishList;
import com.github.anthony_o.swingifts.service.dao.PersonDao;
import com.github.anthony_o.swingifts.service.dao.ReservationDao;
import com.github.anthony_o.swingifts.service.dao.WishItemDao;
import com.github.anthony_o.swingifts.service.dao.WishListDao;
import com.github.anthony_o.swingifts.util.ServiceUtils;
import com.google.inject.Singleton;

import javax.ws.rs.ForbiddenException;
import java.util.List;

@Singleton
public class WishListService {


    private WishListDao getWishListDao() {
        return ServiceUtils.attachIfTransactionElseOnDemand(WishListDao.class);
    }

    private WishItemDao getWishItemDao() {
        return ServiceUtils.attachIfTransactionElseOnDemand(WishItemDao.class);
    }

    private ReservationDao getReservationDao() {
        return ServiceUtils.attachIfTransactionElseOnDemand(ReservationDao.class);
    }

    private PersonDao getPersonDao() {
        return ServiceUtils.attachIfTransactionElseOnDemand(PersonDao.class);
    }


    public WishList findOneWithEventIdAndPersonIdAndAskerPersonIdLoadingWishItemsAndReservations(long eventId, long personId, long askerPersonId) {
        WishListDao wishListDao = getWishListDao();
        WishItemDao wishItemDao = getWishItemDao();

        // first check if the asker is in the event
        ServiceUtils.checkThatAskerIsInEventWithEventIdAndAskerPersonId(eventId, askerPersonId, wishListDao);

        WishList wishList;

        List<WishItem> wishItems = wishItemDao.findWithPersonId(personId);
        if (personId != askerPersonId) {
            // If the asker doesn't try to see its own wishlist, then he/she can see all the items attached to them
            wishList = wishListDao.findOneWithEventIdAndPersonIdUsingPublicProjection(eventId, personId);

            wishItems.addAll(wishItemDao.findWithWishListId(wishList.getId()));
            // And he/she also can see the reservations on each wishitem
            ReservationDao reservationDao = getReservationDao();
            for (WishItem wishItem : wishItems) {
                wishItem.setReservations(reservationDao.findWithWishItemId(wishItem.getId()));
            }
        } else {
            // load with rights
            wishList = wishListDao.findOneWithEventIdAndPersonIdUsingPrivateProjection(eventId, personId);
        }

        wishList.setWishItems(wishItems);
        return wishList;
    }

    public void readCircleGiftTargetPersonIdWithIdAndAskerPersonId(long id, long askerPersonId) {
        WishListDao wishListDao = getWishListDao();
        checkThatAskerOwnsThisWishListWithAskerPersonIdAndId(askerPersonId, id, wishListDao);
        if (wishListDao.updateIsCircleGiftTargetPersonIdReadWithIdAndIsCircleGiftTargetPersonIdRead(id, true) != 1) {
            throw new IllegalStateException("Problem happened when updating the circleGiftTargetPersonIdRead");
        }
    }

    private void checkThatAskerOwnsThisWishListWithAskerPersonIdAndId(long askerPersonId, long id, WishListDao wishListDao) {
        if (wishListDao.countWithIdAndPersonId(id, askerPersonId) < 1) {
            throw new ForbiddenException("Only the one who possess this wishlist can modify circleGiftTargetPersonIdRead");
        }
    }

    public void updateWithWishListAndAskerPersonId(WishList wishList, long askerPersonId) {
        WishListDao wishListDao = getWishListDao();
        Long id = wishList.getId();
        checkThatAskerOwnsThisWishListWithAskerPersonIdAndId(askerPersonId, id, wishListDao);
        if (wishListDao.update(wishList) != 1) {
            throw new IllegalStateException("Problem happened while updating the wishList");
        }
    }

    public List<WishList> findWithEventIdAndAskerPersonIdLoadingPersonAndCountingWishItemsOrderByPersonName(long eventId, long askerPersonId) {
        WishListDao wishListDao = getWishListDao();
        PersonDao personDao = getPersonDao();

        ServiceUtils.checkThatAskerIsInEventWithEventIdAndAskerPersonId(eventId, askerPersonId, wishListDao);
        List<WishList> wishLists = wishListDao.findWithEventIdAndAskerPersonIdCountingWishItemsOrderByPersonName(eventId, askerPersonId);
        for (WishList wishList : wishLists) {
            Long personId = wishList.getPersonId();
            Person person;
            if (personId == askerPersonId) {
                person = personDao.findOneUsingPrivateProjection(personId);
            } else {
                person = personDao.findOneUsingPupblicProjection(personId);
            }
            wishList.setPerson(person);
        }
        return wishLists;
    }
}
