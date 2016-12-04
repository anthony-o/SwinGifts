package com.github.anthony_o.swingifts.service;

import com.github.anthony_o.swingifts.entity.WishItem;
import com.github.anthony_o.swingifts.service.dao.WishItemDao;
import com.github.anthony_o.swingifts.test.CreateSampleDbTest;
import com.github.anthony_o.swingifts.util.InjectUtils;
import com.github.anthony_o.swingifts.util.ServiceUtils;
import org.junit.Test;

import javax.ws.rs.ForbiddenException;

import static org.assertj.core.api.Assertions.assertThat;

public class WishItemServiceTest extends CreateSampleDbTest {

    private WishItemService getWishItemService() {
        return InjectUtils.getInstance(WishItemService.class);
    }

    private WishItemDao getWishItemDao() {
        return ServiceUtils.attachIfTransactionElseOnDemand(WishItemDao.class);
    }


    @Test
    public void createWithWishItemAndAskerPersonIdTest() {
        WishItemService wishItemService = getWishItemService();
        WishItem wishItemForAliceCreatedByAliceToCreate = new WishItem();
        wishItemForAliceCreatedByAliceToCreate.setWishListId(aliceWishListId);
        wishItemForAliceCreatedByAliceToCreate.setName("Create for Alice by Alice");
        final long wishItemForAliceCreatedByAliceId = wishItemService.createWithWishItemAndAskerPersonId(wishItemForAliceCreatedByAliceToCreate, alicePersonId);
        WishItemDao wishItemDao = getWishItemDao();
        WishItem wishItemForAliceCreatedByAlice = wishItemDao.findOne(wishItemForAliceCreatedByAliceId);
        assertThat(wishItemForAliceCreatedByAlice.getPersonId()).isEqualTo(alicePersonId);
        assertThat(wishItemForAliceCreatedByAlice.getWishListId()).isNull();

        WishItem wishItemForAliceCreatedByBobToCreate = new WishItem();
        wishItemForAliceCreatedByBobToCreate.setWishListId(aliceWishListId);
        wishItemForAliceCreatedByBobToCreate.setName("Create for Alice by Bob");
        final long wishItemForAliceCreatedByBobId = wishItemService.createWithWishItemAndAskerPersonId(wishItemForAliceCreatedByBobToCreate, bobPersonId);
        WishItem wishItemForAliceCreatedByBob = wishItemDao.findOne(wishItemForAliceCreatedByBobId);
        assertThat(wishItemForAliceCreatedByBob.getPersonId()).isNull();
        assertThat(wishItemForAliceCreatedByBob.getWishListId()).isEqualTo(alicePersonId);
    }

    @Test
    public void findWithIdAndAskerPersonIdLoadingReservationsTest() {
        WishItemService wishItemService = getWishItemService();

        WishItem wishItem = wishItemService.findWithIdAndAskerPersonIdLoadingReservations(testPersonalWishItem2ForBobWishItemId, alicePersonId);
        assertThat(wishItem.getReservations()).hasSize(1);

        wishItem = wishItemService.findWithIdAndAskerPersonIdLoadingReservations(testPersonalWishItem2ForBobWishItemId, bobPersonId);
        assertThat(wishItem.getReservations()).isNull();
    }

    @Test
    public void updateWithWishItemAndAskerPersonIdTest() throws Exception {
        WishItemService wishItemService = getWishItemService();
        WishItem wishItem = new WishItem();
        wishItem.setId(deathStarForAliceWishItemId);
        wishItem.setName("update");
        wishItemService.updateWithWishItemAndAskerPersonId(wishItem, bobPersonId);
    }

    @Test
    public void updateWithWishItemAndAskerPersonIdPersonalTest() throws Exception {
        WishItemService wishItemService = getWishItemService();
        WishItem wishItem = new WishItem();
        wishItem.setId(testPersonalWishItem1ForBobWishItemId);
        wishItem.setName("update");
        wishItemService.updateWithWishItemAndAskerPersonId(wishItem, bobPersonId);
    }

    @Test(expected = ForbiddenException.class)
    public void updateWithWishItemAndAskerPersonIdPersonalByAnotherPersonTest() throws Exception {
        WishItemService wishItemService = getWishItemService();
        WishItem wishItem = new WishItem();
        wishItem.setId(testPersonalWishItem1ForBobWishItemId);
        wishItem.setName("update");
        wishItemService.updateWithWishItemAndAskerPersonId(wishItem, alicePersonId);
    }

    @Test(expected = ForbiddenException.class)
    public void updateWithWishItemAndAskerPersonIdByOwnerOfTheWishListTest() throws Exception {
        WishItemService wishItemService = getWishItemService();
        WishItem wishItem = new WishItem();
        wishItem.setId(deathStarForAliceWishItemId);
        wishItem.setName("update");
        wishItemService.updateWithWishItemAndAskerPersonId(wishItem, alicePersonId);
    }

    @Test(expected = ForbiddenException.class)
    public void updateWithWishItemAndAskerPersonIdByAPersonNotInTheEventTest() throws Exception {
        WishItemService wishItemService = getWishItemService();
        WishItem wishItem = new WishItem();
        wishItem.setId(deathStarForAliceWishItemId);
        wishItem.setName("update");
        wishItemService.updateWithWishItemAndAskerPersonId(wishItem, charliePersonId);
    }

    @Test
    public void deleteWithIdAndAskerPersonIdTest() throws Exception {
        WishItemService wishItemService = getWishItemService();
        wishItemService.deleteWithIdAndAskerPersonId(deathStarForAliceWishItemId, bobPersonId);

        assertThat(getWishItemDao().findOne(deathStarForAliceWishItemId)).isNull();
    }

    @Test(expected = ForbiddenException.class)
    public void deleteWithIdAndAskerPersonIdByAPersonNotInTheEventTest() throws Exception {
        WishItemService wishItemService = getWishItemService();
        wishItemService.deleteWithIdAndAskerPersonId(deathStarForAliceWishItemId, charliePersonId);
    }

    @Test(expected = ForbiddenException.class)
    public void deleteWithIdAndAskerPersonIdByTheOwnerOfTheWishListTest() throws Exception {
        WishItemService wishItemService = getWishItemService();
        wishItemService.deleteWithIdAndAskerPersonId(deathStarForAliceWishItemId, alicePersonId);
    }

    @Test
    public void deleteWithIdAndAskerPersonIdOnPersonalWishItemTest() throws Exception {
        WishItemService wishItemService = getWishItemService();
        wishItemService.deleteWithIdAndAskerPersonId(testPersonalWishItem1ForBobWishItemId, bobPersonId);

        assertThat(getWishItemDao().findOne(testPersonalWishItem1ForBobWishItemId)).isNull();
    }

    @Test(expected = ForbiddenException.class)
    public void deleteWithIdAndAskerPersonIdOnPersonalWishItemByAPersonSharingAnEventWithOwnerTest() throws Exception {
        WishItemService wishItemService = getWishItemService();
        wishItemService.deleteWithIdAndAskerPersonId(testPersonalWishItem1ForBobWishItemId, alicePersonId);
    }

    @Test
    public void deleteWithIdAndAskerPersonIdOnPersonalWishItemWithReservationsTest() throws Exception {
        WishItemService wishItemService = getWishItemService();
        wishItemService.deleteWithIdAndAskerPersonId(testPersonalWishItem1ForAliceWishItemId, alicePersonId);

        assertThat(getWishItemDao().findOne(testPersonalWishItem1ForAliceWishItemId)).isNull();
    }
}
