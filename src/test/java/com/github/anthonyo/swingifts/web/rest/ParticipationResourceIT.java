package com.github.anthonyo.swingifts.web.rest;

import com.github.anthonyo.swingifts.SwinGiftsApp;
import com.github.anthonyo.swingifts.TestConstants;
import com.github.anthonyo.swingifts.domain.Participation;
import com.github.anthonyo.swingifts.domain.Event;
import com.github.anthonyo.swingifts.domain.User;
import com.github.anthonyo.swingifts.repository.ParticipationRepository;
import com.github.anthonyo.swingifts.service.ParticipationService;
import com.github.anthonyo.swingifts.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static com.github.anthonyo.swingifts.TestConstants.*;
import static com.github.anthonyo.swingifts.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ParticipationResource} REST controller.
 */
@SpringBootTest(classes = SwinGiftsApp.class)
public class ParticipationResourceIT {

    private static final Integer DEFAULT_NB_OF_GIFT_TO_RECEIVE = 0;
    private static final Integer UPDATED_NB_OF_GIFT_TO_RECEIVE = 1;

    private static final Integer DEFAULT_NB_OF_GIFT_TO_DONATE = 0;
    private static final Integer UPDATED_NB_OF_GIFT_TO_DONATE = 1;

    private static final String DEFAULT_USER_ALIAS = "AAAAAAAAAA";
    private static final String UPDATED_USER_ALIAS = "BBBBBBBBBB";

    @Autowired
    private ParticipationRepository participationRepository;

    @Autowired
    private ParticipationService participationService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restParticipationMockMvc;

    private Participation participation;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ParticipationResource participationResource = new ParticipationResource(participationService);
        this.restParticipationMockMvc = MockMvcBuilders.standaloneSetup(participationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Participation createEntity(EntityManager em) {
        Participation participation = new Participation()
            .nbOfGiftToReceive(DEFAULT_NB_OF_GIFT_TO_RECEIVE)
            .nbOfGiftToDonate(DEFAULT_NB_OF_GIFT_TO_DONATE)
            .userAlias(DEFAULT_USER_ALIAS);
        // Add required entity
        Event event;
        if (TestUtil.findAll(em, Event.class).isEmpty()) {
            event = EventResourceIT.createEntity(em);
            em.persist(event);
            em.flush();
        } else {
            event = TestUtil.findAll(em, Event.class).get(0);
        }
        participation.setEvent(event);
        return participation;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Participation createUpdatedEntity(EntityManager em) {
        Participation participation = new Participation()
            .nbOfGiftToReceive(UPDATED_NB_OF_GIFT_TO_RECEIVE)
            .nbOfGiftToDonate(UPDATED_NB_OF_GIFT_TO_DONATE)
            .userAlias(UPDATED_USER_ALIAS);
        // Add required entity
        Event event;
        if (TestUtil.findAll(em, Event.class).isEmpty()) {
            event = EventResourceIT.createUpdatedEntity(em);
            em.persist(event);
            em.flush();
        } else {
            event = TestUtil.findAll(em, Event.class).get(0);
        }
        participation.setEvent(event);
        return participation;
    }

    @BeforeEach
    public void initTest() {
        participation = createEntity(em);
    }

    @Test
    @Transactional
    public void createParticipation() throws Exception {
        int databaseSizeBeforeCreate = participationRepository.findAll().size();

        // Create the Participation
        restParticipationMockMvc.perform(post("/api/participations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(participation)))
            .andExpect(status().isCreated());

        // Validate the Participation in the database
        List<Participation> participationList = participationRepository.findAll();
        assertThat(participationList).hasSize(databaseSizeBeforeCreate + 1);
        Participation testParticipation = participationList.get(participationList.size() - 1);
        assertThat(testParticipation.getNbOfGiftToReceive()).isEqualTo(DEFAULT_NB_OF_GIFT_TO_RECEIVE);
        assertThat(testParticipation.getNbOfGiftToDonate()).isEqualTo(DEFAULT_NB_OF_GIFT_TO_DONATE);
        assertThat(testParticipation.getUserAlias()).isEqualTo(DEFAULT_USER_ALIAS);
    }

    @Test
    @Transactional
    @WithMockUser("alice")
    public void createParticipationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = participationRepository.findAll().size();

        // Create the Participation with an existing ID
        participation.setId(PARTICIPATION_ALICE_IN_ALICE_S_EVENT_ID);

        // An entity with an existing ID cannot be created, so this API call must fail
        restParticipationMockMvc.perform(post("/api/participations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(participation)))
            .andExpect(status().isBadRequest());

        // Validate the Participation in the database
        List<Participation> participationList = participationRepository.findAll();
        assertThat(participationList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkUserAliasIsRequired() throws Exception {
        int databaseSizeBeforeTest = participationRepository.findAll().size();
        // set the field null
        participation.setUserAlias(null);

        // Create the Participation, which fails.

        restParticipationMockMvc.perform(post("/api/participations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(participation)))
            .andExpect(status().isBadRequest());

        List<Participation> participationList = participationRepository.findAll();
        assertThat(participationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @WithMockUser("alice")
    public void getAllParticipationsByEventId() throws Exception {
        // Get all the participationList
        restParticipationMockMvc.perform(get("/api/participations/by-event-id/{eventId}", EVENT_ALICES_EVENT_ID))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasSize(5)))
            .andExpect(jsonPath("$.[*].id").value(hasItem((int)PARTICIPATION_ALICE_IN_ALICE_S_EVENT_ID)))
            .andExpect(jsonPath("$.[0].nbOfGiftToReceive").value(1))
            .andExpect(jsonPath("$.[0].nbOfGiftToDonate").value(1))
            .andExpect(jsonPath("$.[0].userAlias").value("Al'"));
    }

    @Test
    @Transactional
    @WithMockUser("alice")
    public void getParticipation() throws Exception {
        // Get the participation
        restParticipationMockMvc.perform(get("/api/participations/{id}", PARTICIPATION_ALICE_IN_ALICE_S_EVENT_ID))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value((int) PARTICIPATION_ALICE_IN_ALICE_S_EVENT_ID))
            .andExpect(jsonPath("$.nbOfGiftToReceive").value(1))
            .andExpect(jsonPath("$.nbOfGiftToDonate").value(1))
            .andExpect(jsonPath("$.userAlias").value("Al'"));
    }

    @Test
    @Transactional
    @WithMockUser("alice")
    public void getNonExistingParticipation() throws Exception {
        // Get the participation
        restParticipationMockMvc.perform(get("/api/participations/{id}", Long.MAX_VALUE))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    public void updateNonExistingParticipation() throws Exception {
        int databaseSizeBeforeUpdate = participationRepository.findAll().size();

        // Create the Participation

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParticipationMockMvc.perform(put("/api/participations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(participation)))
            .andExpect(status().isBadRequest());

        // Validate the Participation in the database
        List<Participation> participationList = participationRepository.findAll();
        assertThat(participationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    @WithMockUser("alice")
    public void deleteParticipation() throws Exception {
        int databaseSizeBeforeDelete = participationRepository.findAll().size();

        // Delete the participation
        restParticipationMockMvc.perform(delete("/api/participations/{id}", PARTICIPATION_ERIN_IN_ALICE_S_EVENT_ID)
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Participation> participationList = participationRepository.findAll();
        assertThat(participationList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    @WithMockUser("frank")
    public void deleteParticipationNotInEvent() throws Exception {
        int databaseSizeBeforeDelete = participationRepository.findAll().size();

        // Delete the participation
        restParticipationMockMvc.perform(delete("/api/participations/{id}", PARTICIPATION_ERIN_IN_ALICE_S_EVENT_ID)
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isForbidden());

        // Validate the database contains one less item
        List<Participation> participationList = participationRepository.findAll();
        assertThat(participationList).hasSize(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Participation.class);
        Participation participation1 = new Participation();
        participation1.setId(1L);
        Participation participation2 = new Participation();
        participation2.setId(participation1.getId());
        assertThat(participation1).isEqualTo(participation2);
        participation2.setId(2L);
        assertThat(participation1).isNotEqualTo(participation2);
        participation1.setId(null);
        assertThat(participation1).isNotEqualTo(participation2);
    }

    @Test
    @Transactional
    @WithMockUser("erin")
    public void updateParticipationWithUserNull() throws Exception {
        // Initialize the database
        int databaseSizeBeforeUpdate = participationRepository.findAll().size();

        // Update the participation
        Participation updatedParticipation = new Participation();
        updatedParticipation.setId(PARTICIPATION_USER_2_IN_ERIN_S_EVENT_ID);
        updatedParticipation
            .nbOfGiftToReceive(UPDATED_NB_OF_GIFT_TO_RECEIVE)
            .nbOfGiftToDonate(UPDATED_NB_OF_GIFT_TO_DONATE)
            .userAlias(UPDATED_USER_ALIAS)
            .event(new Event())
            .user(new User())
        ;

        restParticipationMockMvc.perform(put("/api/participations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedParticipation)))
            .andExpect(status().isOk());

        // Validate the Participation in the database
        List<Participation> participationList = participationRepository.findAll();
        assertThat(participationList).hasSize(databaseSizeBeforeUpdate);
        Participation testParticipation = participationRepository.findById(PARTICIPATION_USER_2_IN_ERIN_S_EVENT_ID).get();
        assertThat(testParticipation.getNbOfGiftToReceive()).isEqualTo(UPDATED_NB_OF_GIFT_TO_RECEIVE);
        assertThat(testParticipation.getNbOfGiftToDonate()).isEqualTo(UPDATED_NB_OF_GIFT_TO_DONATE);
        assertThat(testParticipation.getUserAlias()).isEqualTo(UPDATED_USER_ALIAS);
    }

    @Test
    @Transactional
    @WithMockUser("alice")
    public void updateParticipationWithUserNullNotInEvent() throws Exception {
        // Update the participation
        Participation updatedParticipation = new Participation();
        updatedParticipation.setId(PARTICIPATION_USER_2_IN_ERIN_S_EVENT_ID);
        updatedParticipation
            .nbOfGiftToReceive(UPDATED_NB_OF_GIFT_TO_RECEIVE)
            .nbOfGiftToDonate(UPDATED_NB_OF_GIFT_TO_DONATE)
            .userAlias(UPDATED_USER_ALIAS)
            .event(new Event())
            .user(new User())
        ;

        restParticipationMockMvc.perform(put("/api/participations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedParticipation)))
            .andExpect(status().isForbidden());
    }
}
