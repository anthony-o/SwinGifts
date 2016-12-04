package com.github.anthony_o.swingifts.service;

import com.github.anthony_o.swingifts.entity.WishItem;
import com.github.anthony_o.swingifts.service.dao.ReservationDao;
import com.github.anthony_o.swingifts.service.dao.WishItemDao;
import com.github.anthony_o.swingifts.service.dao.WishListDao;
import com.github.anthony_o.swingifts.util.ServiceUtils;
import com.google.inject.Singleton;

import javax.ws.rs.ForbiddenException;

@Singleton
public class WishItemService {


    private WishItemDao getWishItemDao() {
        return ServiceUtils.attachIfTransactionElseOnDemand(WishItemDao.class);
    }

    private WishListDao getWishListDao() {
        return ServiceUtils.attachIfTransactionElseOnDemand(WishListDao.class);
    }

    private ReservationDao getReservationDao() {
        return ServiceUtils.attachIfTransactionElseOnDemand(ReservationDao.class);
    }


    public long createWithWishItemAndAskerPersonId(WishItem wishItem, long askerPersonId) {
        Long wishListId = wishItem.getWishListId();
        if (wishListId != null) {
            // in order to add it to this wishlist, we must be sure that the asker is not him/herself, in that case, we create the wishitem outside of the wishlist: directly connected to him/her
            WishListDao wishListDao = getWishListDao();
            if (wishListDao.countWithIdAndPersonId(wishListId, askerPersonId) > 0) {
                // the target wishlist is the asker's one, let's set it on him/her own wishlist
                wishItem.setPersonId(askerPersonId);
                wishItem.setWishListId(null);
            }
            // check that the user is in the event
            ServiceUtils.checkThatAskerIsInEventWithWishListIdAndAskerPersonId(wishListId, askerPersonId, wishListDao);
        }
        wishItem.setCreatedByPersonId(askerPersonId);
        return getWishItemDao().create(wishItem);
    }

    public WishItem findWithIdAndAskerPersonIdLoadingReservations(long id, long askerPersonId) {
        // assert that the asker is in the event
        ServiceUtils.checkThatAskerIsInEventOrOwnsWishItemOrShareEventWithWishItemOwnerWithWishItemIdAndAskerPersonId(id, askerPersonId);
        WishItem wishItem = getWishItemDao().findOneForUI(id);
        Long wishItemPersonId = wishItem.getPersonId();
        if (wishItemPersonId != null && wishItemPersonId != askerPersonId) {
            // loading reservations only if the asker is not the one to whom this wishitem belongs to
            wishItem.setReservations(getReservationDao().findWithWishItemId(id));
        }
        return wishItem;
    }

    public void updateWithWishItemAndAskerPersonId(WishItem wishItem, long askerPersonId) throws Exception {
        ServiceUtils.inTransaction(() -> {
            WishItemDao wishItemDao = getWishItemDao();

            // assert that the asker is in the event
            ServiceUtils.checkThatAskerIsInEventAndDoesntOwnTheWishItemSWishListOrOwnsWishItemWithWishItemIdAndAskerPersonId(wishItem.getId(), askerPersonId, wishItemDao);

            if (wishItemDao.update(wishItem) != 1) {
                throw new IllegalStateException("Problem while updating a WishItem");
            }
            return null;
        });
    }

    public void deleteWithIdAndAskerPersonId(long id, long askerPersonId) throws Exception {
        ServiceUtils.inTransaction(() -> {
            WishItemDao wishItemDao = getWishItemDao();

            ServiceUtils.checkThatAskerIsInEventAndDoesntOwnTheWishItemSWishListOrOwnsWishItemWithWishItemIdAndAskerPersonId(id, askerPersonId, wishItemDao);

            if (wishItemDao.deleteWithReservations(id) != 1) {
                throw new IllegalStateException("Error while deleting a wishItem");
            }

            return null;
        });
    }
}
