package com.github.anthonyo.swingifts.service;

import com.github.anthonyo.swingifts.SwinGiftsApp;
import com.github.anthonyo.swingifts.domain.Event;
import com.github.anthonyo.swingifts.domain.GiftDrawing;
import com.github.anthonyo.swingifts.domain.GiftIdea;
import com.github.anthonyo.swingifts.repository.EventRepository;
import com.github.anthonyo.swingifts.service.errors.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    public void findOneWithEagerRelationshipsTest() {
        // WHEN
        final Optional<Event> optionalEvent = eventService.findOneWithEagerRelationships(EVENT_ALICES_EVENT_ID, USER_ALICE_LOGIN);
        // THEN
        assertThat(optionalEvent).isPresent();
        final Event event = optionalEvent.get();
        final Set<GiftIdea> allGiftIdeas = event.getParticipations().stream().flatMap(participation -> participation.getGiftIdeas().stream()).collect(Collectors.toUnmodifiableSet());
        assertThat(allGiftIdeas).hasSize(4);
        assertThat(allGiftIdeas.stream().map(GiftIdea::getId)).contains(GIFT_IDEA_ALICE_S_IDEA_FOR_ALICE);
        assertThat(allGiftIdeas.stream().map(GiftIdea::getId)).doesNotContain(GIFT_IDEA_DAVE_S_IDEA_FOR_ALICE);
    }

    @Test
    @Transactional
    public void drawGiftsTest() throws EntityNotFoundException {
        for (int i = 0; i < 50; i++) {
            // Check this 50 times because it is random
            eventService.drawGifts(EVENT_ALICES_EVENT_ID, USER_ALICE_LOGIN);

            Event aliceSEvent = eventRepository.findById(EVENT_ALICES_EVENT_ID).get();
            assertThat(aliceSEvent.getGiftDrawings()).hasSize(5);
            for (GiftDrawing giftDrawing : aliceSEvent.getGiftDrawings()) {
                // Check that exclusion groups are working
                if (giftDrawing.getDonor().getUser().getLogin().equals(USER_ALICE_LOGIN)) {
                    assertThat(giftDrawing.getRecipient().getUser().getLogin()).isNotEqualTo(USER_BOB_LOGIN);
                }
                if (giftDrawing.getDonor().getUser().getLogin().equals(USER_BOB_LOGIN)) {
                    assertThat(giftDrawing.getRecipient().getUser().getLogin()).isNotEqualTo(USER_ALICE_LOGIN);
                }
            }

            eventService.drawGifts(EVENT_BOBS_EVENT_ID, USER_BOB_LOGIN);

            Event bobSEvent = eventRepository.findById(EVENT_BOBS_EVENT_ID).get();
            assertThat(bobSEvent.getGiftDrawings()).hasSize(5);
            for (GiftDrawing giftDrawing : bobSEvent.getGiftDrawings()) {
                // Check that exclusion groups are working
                if (giftDrawing.getDonor().getUser().getLogin().equals(USER_ALICE_LOGIN)) {
                    assertThat(giftDrawing.getRecipient().getUser().getLogin()).isNotEqualTo(USER_CHARLOTTE_LOGIN);
                }
                if (giftDrawing.getDonor().getUser().getLogin().equals(USER_CHARLOTTE_LOGIN)) {
                    assertThat(giftDrawing.getRecipient().getUser().getLogin()).isNotEqualTo(USER_ALICE_LOGIN);
                }
            }

            eventService.drawGifts(EVENT_CHARLOTTES_EVENT_ID, USER_CHARLOTTE_LOGIN);

            Event charlotteSEvent = eventRepository.findById(EVENT_BOBS_EVENT_ID).get();
            assertThat(charlotteSEvent.getGiftDrawings()).hasSize(5);
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
