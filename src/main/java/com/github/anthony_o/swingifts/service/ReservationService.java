package com.github.anthony_o.swingifts.service;

import com.github.anthony_o.swingifts.entity.Reservation;
import com.github.anthony_o.swingifts.service.dao.ReservationDao;
import com.github.anthony_o.swingifts.service.dao.WishItemDao;
import com.github.anthony_o.swingifts.util.ServiceUtils;
import com.google.inject.Inject;

import javax.ws.rs.ForbiddenException;

public class ReservationService {

    @Inject
    private WishItemService wishItemService;

    private ReservationDao getReservationDao() {
        return ServiceUtils.attachIfTransactionElseOnDemand(ReservationDao.class);
    }

    private WishItemDao getWishItemDao() {
        return ServiceUtils.attachIfTransactionElseOnDemand(WishItemDao.class);
    }

    public long createWithReservationAndAskerPersonId(Reservation reservation, long askerPersonId) {
        // Checking that the asker is not the one to whom the wishitem belongs
        Long wishItemId = reservation.getWishItemId();
        Long wishItemPersonId = getWishItemDao().findOnePersonIdSearchingUpToWishListIfNull(wishItemId);
        checkRightsWithWishItemIdAndWishItemPersonIdAndAskerPersonId(wishItemId, wishItemPersonId, askerPersonId);
        if (reservation.getPersonId() == null) {
            // set the reservation to the askerPersonId if not specified
            reservation.setPersonId(askerPersonId);
        }
        return getReservationDao().create(reservation);
    }

    public void checkRightsWithWishItemIdAndWishItemPersonIdAndAskerPersonId(long wishItemId, long wishItemPersonId, long askerPersonId) {
        if (wishItemPersonId == askerPersonId) {
            throw new ForbiddenException("You can't manage your own wishitem reservation");
        }
        // must be sure that the asker is in the event
        ServiceUtils.checkThatAskerIsInEventOrShareEventWithWishItemOwnerWithWishItemIdAndAskerPersonId(wishItemId, askerPersonId);
    }

    public void deleteWithIdAndAskerPersonId(long id, long askerPersonId) {
        ReservationDao reservationDao = getReservationDao();
        Long wishItemId = reservationDao.findOneWishItemId(id);
        Long wishItemPersonId = getWishItemDao().findOnePersonIdSearchingUpToWishListIfNull(wishItemId);

        checkRightsWithWishItemIdAndWishItemPersonIdAndAskerPersonId(wishItemId, wishItemPersonId, askerPersonId);

        if (reservationDao.delete(id) != 1) {
            throw new IllegalStateException("Problem while deleting");
        }
    }
}
