package com.github.anthony_o.swingifts.service;

import com.github.anthony_o.swingifts.entity.Event;
import com.github.anthony_o.swingifts.entity.WishList;
import com.github.anthony_o.swingifts.service.dao.EventDao;
import com.github.anthony_o.swingifts.service.dao.WishListDao;
import com.github.anthony_o.swingifts.util.Base64Utils;
import com.github.anthony_o.swingifts.util.ServiceUtils;
import com.google.inject.Singleton;

import java.security.SecureRandom;
import java.util.List;

@Singleton
public class EventService {
    public static int KEY_LENGTH = 33;


    private EventDao getEventDao() {
        return ServiceUtils.attachIfTransactionElseOnDemand(EventDao.class);
    }

    private WishListDao getWishListDao() {
        return ServiceUtils.attachIfTransactionElseOnDemand(WishListDao.class);
    }

    public Event findOneWithIdAndKey(long id, byte[] key) {
        return getEventDao().findOneWithIdAndKey(id, key);
    }

    public Event findOneWithIdAndKeyAndAskerPersonId(long id, Long askerPersonId) {
        // the asker must have a wishlist in this event
        ServiceUtils.checkThatAskerIsInEventWithEventIdAndAskerPersonId(id, askerPersonId);
        return getEventDao().findOneWithId(id);
    }

    public List<Event> findWithAskerPersonId(long askerPersonId) {
        return getEventDao().findWithAskerPersonId(askerPersonId);
    }

    public Event createThenCreateAdminWishListWithEventAndAskerPersonId(Event event, long askerPersonId) throws Exception {
        return ServiceUtils.inTransaction(() -> {
            byte[] key = new byte[KEY_LENGTH];
            new SecureRandom().nextBytes(key);
            long id = getEventDao().createWithEventAndKey(event, key);
            event.setId(id);
            event.setKey(Base64Utils.convertFromBytesToBase64RFC4648(key));

            getWishListDao().createAdminWithEventIdAndPersonId(id, askerPersonId);

            return event;
        });
    }

    public WishList launchCircleGiftWithIdAndAskerPersonIdReturningAskersWishList(long id, long askerPersonId) throws Exception {
        return ServiceUtils.inTransaction(() -> {
            // first check if asker is admin
            WishListDao wishListDao = getWishListDao();
            ServiceUtils.checkThatAskerIsAdminWithEventIdAndAskerPersonId(id, askerPersonId, wishListDao);

            List<Long> personIds = wishListDao.findPersonsParticipatingInCircleGiftWithEventIdReturningPersonId(id);
            // now let's shuffle this array (inspired by http://stackoverflow.com/a/1520212/535203 )
            int numberOfPersons = personIds.size();
            SecureRandom secureRandom = new SecureRandom();
            for (int i = 0; i < numberOfPersons; i++) {
                int randomIndex = secureRandom.nextInt(numberOfPersons - i) + i;
                // swap the two values
                Long randomedPersonId = personIds.get(randomIndex);
                personIds.set(randomIndex, personIds.get(i));
                personIds.set(i, randomedPersonId);
            }
            // now let's update all the CIRCLE_GIFT_TARGET_PERSON_ID to the neighbour of each personId in the array
            Long askerCircleGiftTargetPersonId = null;
            for (int i = 0; i < numberOfPersons; i++) {
                Long personIdToUpdate = personIds.get(i);
                Long circleGiftTargetPersonId = personIds.get((i + 1) % numberOfPersons);
                if (wishListDao.updateCircleGiftTargetPersonIdWithEventIdAndPersonIdAndCircleGiftTargetPersonId(id, personIdToUpdate, circleGiftTargetPersonId) != 1) {
                    throw new IllegalStateException("Couldn't update circleGiftTargetPersonId");
                }
                if (personIdToUpdate == askerPersonId) {
                    askerCircleGiftTargetPersonId = circleGiftTargetPersonId;
                }
            }

            if (getEventDao().updateIsCircleGiftLaunchedOnceWithIdAndCircleGiftLaunchedOnce(id, true) != 1) {
                throw new IllegalStateException("Couldn't update the event to set the CircleGiftLaunchedOnce to true.");
            }

            WishList wishList = new WishList();
            wishList.setCircleGiftTargetPersonId(askerCircleGiftTargetPersonId);

            return wishList;
        });
    }
}
