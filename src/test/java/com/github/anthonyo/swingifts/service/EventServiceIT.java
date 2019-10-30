package com.github.anthonyo.swingifts.service;

import com.github.anthonyo.swingifts.SwinGiftsApp;
import com.github.anthonyo.swingifts.domain.Event;
import com.github.anthonyo.swingifts.domain.GiftIdea;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.anthonyo.swingifts.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = SwinGiftsApp.class)
public class EventServiceIT {

    @Autowired
    private EventService eventService;

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
}
