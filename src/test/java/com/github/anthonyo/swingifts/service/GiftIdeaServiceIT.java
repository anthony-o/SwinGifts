package com.github.anthonyo.swingifts.service;

import com.github.anthonyo.swingifts.SwinGiftsApp;
import com.github.anthonyo.swingifts.domain.GiftIdea;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.github.anthonyo.swingifts.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = SwinGiftsApp.class)
class GiftIdeaServiceIT {
    @Autowired
    private GiftIdeaService giftIdeaService;

    @Test
    void testFindByRecipientIdForRequesterUserLoginForOneself() {
        // WHEN
        final List<GiftIdea> giftIdeas = giftIdeaService.findByRecipientIdForRequesterUserLogin(PARTICIPATION_ALICE_IN_ALICE_S_EVENT_ID, USER_ALICE_LOGIN);
        // THEN
        assertThat(giftIdeas).hasSize(1);
        assertThat(giftIdeas.stream().map(GiftIdea::getId)).contains(GIFT_IDEA_ALICE_S_IDEA_FOR_ALICE);
        assertThat(giftIdeas.stream().map(GiftIdea::getId)).doesNotContain(GIFT_IDEA_DAVE_S_IDEA_FOR_ALICE);
    }
}
