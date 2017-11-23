package com.github.anthony_o.swingifts.service;

import com.github.anthony_o.swingifts.entity.Event;
import com.github.anthony_o.swingifts.entity.WishList;
import com.github.anthony_o.swingifts.service.dao.EventDao;
import com.github.anthony_o.swingifts.service.dao.WishListDao;
import com.github.anthony_o.swingifts.test.CreateSampleDbTest;
import com.github.anthony_o.swingifts.util.InjectUtils;
import com.github.anthony_o.swingifts.util.ServiceUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.junit.Test;

import javax.ws.rs.ForbiddenException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EventServiceTest extends CreateSampleDbTest {

    private EventService getEventService() {
        return InjectUtils.getInstance(EventService.class);
    }

    private EventDao getEventDao() {
        return ServiceUtils.attachIfTransactionElseOnDemand(EventDao.class);
    }

    private WishListDao getWishListDao() {
        return ServiceUtils.attachIfTransactionElseOnDemand(WishListDao.class);
    }

    @Test
    public void findWithPersonIdAndAskerPersonIdTest() {
        List<Event> events = getEventService().findWithAskerPersonId(alicePersonId);
        assertThat(events).hasSize(numberOfEventsThatAliceIsIn);
        assertThat(events.get(0).getIsOpened()).isTrue();

        events = getEventService().findWithAskerPersonId(bobPersonId);
        assertThat(events).hasSize(numberOfEventsThatBobIsIn);
        assertThat(events.get(0).getIsOpened()).isTrue();
    }

    @Test
    public void createThenCreateAdminWishListWithEventAndAskerPersonIdTest() throws Exception {
        EventService eventService = getEventService();

        Event event = new Event();
        event.setName("Test event");
        Event createdEvent = eventService.createThenCreateAdminWishListWithEventAndAskerPersonId(event, alicePersonId);
        assertThat(createdEvent.getId()).isNotNull();
        assertThat(createdEvent.getKey()).isNotNull();

        assertThat(eventService.findWithAskerPersonId(alicePersonId)).hasSize(numberOfEventsThatAliceIsIn + 1);
    }

    @Test
    public void launchCircleGiftWithIdAndAskerPersonIdReturningAskersWishListTest() throws Exception {
        EventService eventService = getEventService();
        EventDao eventDao = getEventDao();

        assertThat(eventDao.findOneWithId(charlieSEventId).getIsCircleGiftLaunchedOnce()).isFalse();

        WishList wishList = eventService.launchCircleGiftWithIdAndAskerPersonIdReturningAskersWishList(charlieSEventId, charliePersonId);
        assertThat(wishList.getCircleGiftTargetPersonId()).isNotNull().isNotEqualTo(charliePersonId);

        assertThat(eventDao.findOneWithId(charlieSEventId).getIsCircleGiftLaunchedOnce()).isTrue();

        // assert that all was randomized
        List<WishList> wishLists = getWishListDao().findWithEventId(charlieSEventId);
        for (WishList currentWishList : wishLists) {
            if (BooleanUtils.isTrue(currentWishList.getIsPersonParticipatesInCircleGift())) {
                assertThat(currentWishList.getCircleGiftTargetPersonId()).isNotNull().isNotEqualTo(currentWishList.getPersonId());
            } else {
                assertThat(currentWishList.getCircleGiftTargetPersonId()).isNull();
            }
        }
    }

    @Test(expected = ForbiddenException.class)
    public void launchCircleGiftWithIdAndAskerPersonIdReturningAskersWishListByNotAdminPersonTest() throws Exception {
        getEventService().launchCircleGiftWithIdAndAskerPersonIdReturningAskersWishList(charlieSEventId, alicePersonId);
    }

    @Test(expected = ForbiddenException.class)
    public void cancelCircleGiftWithIdAndAskerPersonIdReturningAskersWishListByNotAdminPersonTest() throws Exception {
        getEventService().cancelCircleGiftWithIdAndAskerPersonIdReturningAskersWishList(charlieSEventId, alicePersonId);
    }

    @Test
    public void cancelCircleGiftWithIdAndAskerPersonIdReturningAskersWishList() throws Exception {
        WishListDao wishListDao = getWishListDao();
        wishListDao.updateIsCircleGiftTargetPersonIdReadWithIdAndIsCircleGiftTargetPersonIdRead(aliceWishListId, true);

        getEventService().cancelCircleGiftWithIdAndAskerPersonIdReturningAskersWishList(firstEventId, alicePersonId);
        WishList aliceWishList = wishListDao.findOne(aliceWishListId);
        assertThat(aliceWishList.getCircleGiftTargetPersonId()).isNull();
        assertThat(aliceWishList.getIsCircleGiftTargetPersonIdRead()).isFalse();

        assertThat(getEventDao().findOneWithId(firstEventId).getIsCircleGiftLaunchedOnce()).isFalse();
    }

}
