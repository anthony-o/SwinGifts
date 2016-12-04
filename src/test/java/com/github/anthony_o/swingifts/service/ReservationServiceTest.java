package com.github.anthony_o.swingifts.service;

import com.github.anthony_o.swingifts.entity.Reservation;
import com.github.anthony_o.swingifts.test.CreateSampleDbTest;
import com.github.anthony_o.swingifts.util.InjectUtils;
import org.junit.Test;

import javax.ws.rs.ForbiddenException;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationServiceTest extends CreateSampleDbTest {

    @Test
    public void createWithReservationAndAskerPersonIdTest() {
        ReservationService reservationService = InjectUtils.getInstance(ReservationService.class);

        Reservation reservation = new Reservation();
        reservation.setPersonId(alicePersonId);
        reservation.setWishItemId(deathStarForAliceWishItemId);

        assertThat(reservationService.createWithReservationAndAskerPersonId(reservation, bobPersonId)).isNotEqualTo(0L);
    }

    @Test
    public void createWithReservationAndAskerPersonIdWhenReservingAnotherPersonalWishItemTest() {
        ReservationService reservationService = InjectUtils.getInstance(ReservationService.class);

        Reservation reservation = new Reservation();
        reservation.setPersonId(alicePersonId);
        reservation.setWishItemId(testPersonalWishItem1ForBobWishItemId);

        assertThat(reservationService.createWithReservationAndAskerPersonId(reservation, alicePersonId)).isNotEqualTo(0L);
    }

    @Test(expected = ForbiddenException.class)
    public void createWithReservationAndAskerPersonIdWhenSelfReservingTest() {
        ReservationService reservationService = InjectUtils.getInstance(ReservationService.class);

        Reservation reservation = new Reservation();
        reservation.setPersonId(alicePersonId);
        reservation.setWishItemId(deathStarForAliceWishItemId);

        reservationService.createWithReservationAndAskerPersonId(reservation, alicePersonId);
    }

    @Test()
    public void deleteWithIdAndAskerPersonIdTest() {
        ReservationService reservationService = InjectUtils.getInstance(ReservationService.class);

        reservationService.deleteWithIdAndAskerPersonId(barbyForAliceReservedByBobReservationId, bobPersonId);
    }

    @Test(expected = ForbiddenException.class)
    public void deleteWithIdAndAskerPersonIdSelfDeletionTest() {
        ReservationService reservationService = InjectUtils.getInstance(ReservationService.class);

        reservationService.deleteWithIdAndAskerPersonId(barbyForAliceReservedByBobReservationId, alicePersonId);
    }
}
