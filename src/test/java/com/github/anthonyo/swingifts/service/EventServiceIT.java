package com.github.anthonyo.swingifts.service;

import com.github.anthonyo.swingifts.SwinGiftsApp;
import com.github.anthonyo.swingifts.domain.Event;
import com.github.anthonyo.swingifts.domain.GiftDrawing;
import com.github.anthonyo.swingifts.repository.EventRepository;
import com.github.anthonyo.swingifts.service.errors.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import static com.github.anthonyo.swingifts.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = SwinGiftsApp.class)
public class EventServiceIT {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepository;

    @Test
    @Transactional
    public void drawGiftsTest() throws EntityNotFoundException {
        for (int i = 0; i < 50; i++) {
            // Check this 50 times because it is random

            // random draw for a 5 participants, which all have 1 to give, 1 to receive
            eventService.drawGifts(EVENT_ALICES_EVENT_ID, USER_ALICE_LOGIN);

            Event aliceSEvent = eventRepository.findById(EVENT_ALICES_EVENT_ID).get();
            assertThat(aliceSEvent.getGiftDrawings()).hasSize(5);
            assertThat(aliceSEvent.getGiftDrawings().stream().filter(giftDrawing -> giftDrawing.getRecipient().getId() == PARTICIPATION_ALICE_IN_ALICE_S_EVENT_ID)).hasSize(1);
            assertThat(aliceSEvent.getGiftDrawings().stream().filter(giftDrawing -> giftDrawing.getRecipient().getId() == PARTICIPATION_BOB_IN_ALICE_S_EVENT_ID)).hasSize(1);
            assertThat(aliceSEvent.getGiftDrawings().stream().filter(giftDrawing -> giftDrawing.getRecipient().getId() == PARTICIPATION_CHARLOTTE_IN_ALICE_S_EVENT_ID)).hasSize(1);
            assertThat(aliceSEvent.getGiftDrawings().stream().filter(giftDrawing -> giftDrawing.getRecipient().getId() == PARTICIPATION_DAVE_IN_ALICE_S_EVENT_ID)).hasSize(1);
            assertThat(aliceSEvent.getGiftDrawings().stream().filter(giftDrawing -> giftDrawing.getRecipient().getId() == PARTICIPATION_ERIN_IN_ALICE_S_EVENT_ID)).hasSize(1);
            for (GiftDrawing giftDrawing : aliceSEvent.getGiftDrawings()) {
                // Check that exclusion groups are working
                if (giftDrawing.getDonor().getUser().getLogin().equals(USER_ALICE_LOGIN)) {
                    assertThat(giftDrawing.getRecipient().getUser().getLogin()).isNotEqualTo(USER_BOB_LOGIN);
                }
                if (giftDrawing.getDonor().getUser().getLogin().equals(USER_BOB_LOGIN)) {
                    assertThat(giftDrawing.getRecipient().getUser().getLogin()).isNotEqualTo(USER_ALICE_LOGIN);
                }
                // Check that we can't draw a gift to myself
                assertThat(giftDrawing.getRecipient()).isNotEqualTo(giftDrawing.getDonor());
            }

            // random draw for a 4 participants, two have 1/1, another 1/2, another 2/1
            eventService.drawGifts(EVENT_BOBS_EVENT_ID, USER_BOB_LOGIN);

            Event bobSEvent = eventRepository.findById(EVENT_BOBS_EVENT_ID).get();
            assertThat(bobSEvent.getGiftDrawings()).hasSize(5);
            assertThat(bobSEvent.getGiftDrawings().stream().filter(giftDrawing -> giftDrawing.getRecipient().getId() == PARTICIPATION_BOB_IN_BOB_S_EVENT_ID)).hasSize(1);
            assertThat(bobSEvent.getGiftDrawings().stream().filter(giftDrawing -> giftDrawing.getRecipient().getId() == PARTICIPATION_ALICE_IN_BOB_S_EVENT_ID)).hasSize(1);
            assertThat(bobSEvent.getGiftDrawings().stream().filter(giftDrawing -> giftDrawing.getRecipient().getId() == PARTICIPATION_CHARLOTTE_IN_BOB_S_EVENT_ID)).hasSize(2);
            assertThat(bobSEvent.getGiftDrawings().stream().filter(giftDrawing -> giftDrawing.getRecipient().getId() == PARTICIPATION_DAVE_IN_BOB_S_EVENT_ID)).hasSize(1);
            for (GiftDrawing giftDrawing : bobSEvent.getGiftDrawings()) {
                // Check that exclusion groups are working
                if (giftDrawing.getDonor().getUser().getLogin().equals(USER_ALICE_LOGIN)) {
                    assertThat(giftDrawing.getRecipient().getUser().getLogin()).isNotEqualTo(USER_CHARLOTTE_LOGIN);
                }
                if (giftDrawing.getDonor().getUser().getLogin().equals(USER_CHARLOTTE_LOGIN)) {
                    assertThat(giftDrawing.getRecipient().getUser().getLogin()).isNotEqualTo(USER_ALICE_LOGIN);
                }
                // Check that we can't draw a gift to myself
                assertThat(giftDrawing.getRecipient()).isNotEqualTo(giftDrawing.getDonor());
            }

            eventService.drawGifts(EVENT_CHARLOTTES_EVENT_ID, USER_CHARLOTTE_LOGIN);

            Event charlotteSEvent = eventRepository.findById(EVENT_BOBS_EVENT_ID).get();
            assertThat(charlotteSEvent.getGiftDrawings()).hasSize(5);

            // random draw for a 4 participants, which all have 2 to give, 2 to receive
            eventService.drawGifts(EVENT_ERINS_EVENT_ID, USER_ERIN_LOGIN);

            Event erinSEvent = eventRepository.findById(EVENT_ERINS_EVENT_ID).get();
            assertThat(erinSEvent.getGiftDrawings()).hasSize(8);
            assertThat(erinSEvent.getGiftDrawings().stream().filter(giftDrawing -> giftDrawing.getRecipient().getId() == PARTICIPATION_ERIN_IN_ERIN_S_EVENT_ID)).hasSize(2);
            assertThat(erinSEvent.getGiftDrawings().stream().filter(giftDrawing -> giftDrawing.getRecipient().getId() == PARTICIPATION_USER_2_IN_ERIN_S_EVENT_ID)).hasSize(2);
            assertThat(erinSEvent.getGiftDrawings().stream().filter(giftDrawing -> giftDrawing.getRecipient().getId() == PARTICIPATION_USER_3_IN_ERIN_S_EVENT_ID)).hasSize(2);
            assertThat(erinSEvent.getGiftDrawings().stream().filter(giftDrawing -> giftDrawing.getRecipient().getId() == PARTICIPATION_USER_4_IN_ERIN_S_EVENT_ID)).hasSize(2);
        }
    }

    @Test
    @Transactional
    public void drawGiftsNbOfGiftsToReceiveAndToDonateNotEqualTest() {
        assertThrows(IllegalStateException.class, () -> eventService.drawGifts(EVENT_DAVES_EVENT_ID, USER_DAVE_LOGIN));
    }

    @Test
    @Transactional
    public void drawGiftsWrongAdminTest() {
        assertThrows(AccessDeniedException.class, () -> eventService.drawGifts(EVENT_ALICES_EVENT_ID, USER_BOB_LOGIN));
    }
}
