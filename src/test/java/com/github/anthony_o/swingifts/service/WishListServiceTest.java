package com.github.anthony_o.swingifts.service;

import com.github.anthony_o.swingifts.entity.Reservation;
import com.github.anthony_o.swingifts.entity.WishItem;
import com.github.anthony_o.swingifts.entity.WishList;
import com.github.anthony_o.swingifts.service.dao.WishListDao;
import com.github.anthony_o.swingifts.test.CreateSampleDbTest;
import com.github.anthony_o.swingifts.util.InjectUtils;
import com.github.anthony_o.swingifts.util.ServiceUtils;
import org.junit.Test;

import javax.ws.rs.ForbiddenException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class WishListServiceTest extends CreateSampleDbTest {

    private WishListService getWishListService() {
        return InjectUtils.getInstance(WishListService.class);
    }

    private WishListDao getWishListDao() {
        return ServiceUtils.attachIfTransactionElseOnDemand(WishListDao.class);
    }


    @Test
    public void findOneWithEventIdAndPersonIdAndAskerPersonIdLoadingWishItemsAndReservationsTest() {
        WishListService wishListService = getWishListService();

        WishList wishList = wishListService.findOneWithEventIdAndPersonIdAndAskerPersonIdLoadingWishItemsAndReservations(firstEventId, alicePersonId, alicePersonId);
        assertThat(wishList.getIsPersonEventAdmin()).isTrue();
        assertThat(wishList.getIsPersonParticipatesInCircleGift()).isTrue();
        assertThat(wishList.getCircleGiftTargetPersonId()).isEqualTo(bobPersonId);
        List<WishItem> wishItems = wishList.getWishItems();
        assertThat(wishItems).hasSize(2);
        WishItem testPersonalItem1 = wishItems.get(0);
        assertThat(testPersonalItem1.getReservations()).isNull();
        assertThat(testPersonalItem1.getPersonId()).isEqualTo(alicePersonId);
        assertThat(testPersonalItem1.getCreatedByPersonId()).isNull();
        assertThat(wishList.getIsCircleGiftTargetPersonIdRead()).isFalse();

        wishList = wishListService.findOneWithEventIdAndPersonIdAndAskerPersonIdLoadingWishItemsAndReservations(firstEventId, alicePersonId, bobPersonId);
        assertThat(wishList.getIsPersonEventAdmin()).isNull();
        assertThat(wishList.getIsPersonParticipatesInCircleGift()).isTrue();
        assertThat(wishList.getCircleGiftTargetPersonId()).isNull();
        wishItems = wishList.getWishItems();
        assertThat(wishItems).hasSize(4);
        testPersonalItem1 = wishItems.get(0);
        assertThat(testPersonalItem1.getCreatedByPersonId()).isNull();
        assertThat(testPersonalItem1.getPersonId()).isEqualTo(alicePersonId);
        List<Reservation> reservations = testPersonalItem1.getReservations();
        assertThat(reservations).hasSize(1);
        Reservation reservation = reservations.get(0);
        assertThat(reservation.getPersonId()).isEqualTo(bobPersonId);
        WishItem barbyWishItem = wishItems.get(3);
        assertThat(barbyWishItem.getCreatedByPersonId()).isEqualTo(bobPersonId);
        assertThat(wishList.getIsCircleGiftTargetPersonIdRead()).isNull();
    }

    @Test
    public void updateWithWishListAndAskerPersonIdTest() {
        WishListService wishListService = getWishListService();
        WishListDao wishListDao = getWishListDao();

        WishList wishListInDb = wishListDao.findOne(aliceWishListIdInCharlieSEvent);
        assertThat(wishListInDb.getIsPersonParticipatesInCircleGift()).isFalse();

        WishList wishList = new WishList();
        wishList.setId(aliceWishListIdInCharlieSEvent);
        wishList.setIsPersonParticipatesInCircleGift(true);
        wishListService.updateWithWishListAndAskerPersonId(wishList, alicePersonId);

        wishListInDb = wishListDao.findOne(aliceWishListIdInCharlieSEvent);
        assertThat(wishListInDb.getIsPersonParticipatesInCircleGift()).isTrue();
    }

    @Test(expected = ForbiddenException.class)
    public void updateWithWishListAndAskerPersonIdWhenNotOwnerTest() {
        WishListService wishListService = getWishListService();
        WishList wishList = new WishList();
        wishList.setId(aliceWishListIdInCharlieSEvent);
        wishList.setIsPersonParticipatesInCircleGift(true);
        wishListService.updateWithWishListAndAskerPersonId(wishList, bobPersonId);
    }
}
