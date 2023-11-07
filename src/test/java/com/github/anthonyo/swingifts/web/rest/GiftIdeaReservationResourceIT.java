package com.github.anthonyo.swingifts.web.rest;

import com.github.anthonyo.swingifts.SwinGiftsApp;
import com.github.anthonyo.swingifts.domain.GiftIdea;
import com.github.anthonyo.swingifts.domain.GiftIdeaReservation;
import com.github.anthonyo.swingifts.domain.Participation;
import com.github.anthonyo.swingifts.repository.GiftIdeaReservationRepository;
import com.github.anthonyo.swingifts.service.GiftIdeaReservationService;
import com.github.anthonyo.swingifts.web.rest.errors.ExceptionTranslator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.github.anthonyo.swingifts.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the {@link GiftIdeaReservationResource} REST controller.
 */
@SpringBootTest(classes = SwinGiftsApp.class)
public class GiftIdeaReservationResourceIT {

    private static final Instant DEFAULT_CREATION_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATION_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private GiftIdeaReservationRepository giftIdeaReservationRepository;

    @Autowired
    private GiftIdeaReservationService giftIdeaReservationService;

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

    private MockMvc restGiftIdeaReservationMockMvc;

    private GiftIdeaReservation giftIdeaReservation;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        final GiftIdeaReservationResource giftIdeaReservationResource = new GiftIdeaReservationResource(giftIdeaReservationService);
        this.restGiftIdeaReservationMockMvc = MockMvcBuilders.standaloneSetup(giftIdeaReservationResource)
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
    public static GiftIdeaReservation createEntity(EntityManager em) {
        GiftIdeaReservation giftIdeaReservation = new GiftIdeaReservation()
            .creationDate(DEFAULT_CREATION_DATE);
        // Add required entity
        Participation participation;
        if (TestUtil.findAll(em, Participation.class).isEmpty()) {
            participation = ParticipationResourceIT.createEntity(em);
            em.persist(participation);
            em.flush();
        } else {
            participation = TestUtil.findAll(em, Participation.class).get(0);
        }
        giftIdeaReservation.setParticipation(participation);
        // Add required entity
        GiftIdea giftIdea;
        if (TestUtil.findAll(em, GiftIdea.class).isEmpty()) {
            giftIdea = GiftIdeaResourceIT.createEntity(em);
            em.persist(giftIdea);
            em.flush();
        } else {
            giftIdea = TestUtil.findAll(em, GiftIdea.class).get(0);
        }
        giftIdeaReservation.setGiftIdea(giftIdea);
        return giftIdeaReservation;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GiftIdeaReservation createUpdatedEntity(EntityManager em) {
        GiftIdeaReservation giftIdeaReservation = new GiftIdeaReservation()
            .creationDate(UPDATED_CREATION_DATE);
        // Add required entity
        Participation participation;
        if (TestUtil.findAll(em, Participation.class).isEmpty()) {
            participation = ParticipationResourceIT.createUpdatedEntity(em);
            em.persist(participation);
            em.flush();
        } else {
            participation = TestUtil.findAll(em, Participation.class).get(0);
        }
        giftIdeaReservation.setParticipation(participation);
        // Add required entity
        GiftIdea giftIdea;
        if (TestUtil.findAll(em, GiftIdea.class).isEmpty()) {
            giftIdea = GiftIdeaResourceIT.createUpdatedEntity(em);
            em.persist(giftIdea);
            em.flush();
        } else {
            giftIdea = TestUtil.findAll(em, GiftIdea.class).get(0);
        }
        giftIdeaReservation.setGiftIdea(giftIdea);
        return giftIdeaReservation;
    }

    @BeforeEach
    void initTest() {
        giftIdeaReservation = createEntity(em);
    }

//    @Test
    @Transactional
    void createGiftIdeaReservation() throws Exception {
        int databaseSizeBeforeCreate = giftIdeaReservationRepository.findAll().size();

        // Create the GiftIdeaReservation
        restGiftIdeaReservationMockMvc.perform(post("/api/gift-idea-reservations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(giftIdeaReservation)))
            .andExpect(status().isCreated());

        // Validate the GiftIdeaReservation in the database
        List<GiftIdeaReservation> giftIdeaReservationList = giftIdeaReservationRepository.findAll();
        assertThat(giftIdeaReservationList).hasSize(databaseSizeBeforeCreate + 1);
        GiftIdeaReservation testGiftIdeaReservation = giftIdeaReservationList.get(giftIdeaReservationList.size() - 1);
        assertThat(testGiftIdeaReservation.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
    }

    @Test
    @Transactional
    void createGiftIdeaReservationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = giftIdeaReservationRepository.findAll().size();

        // Create the GiftIdeaReservation with an existing ID
        giftIdeaReservation.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restGiftIdeaReservationMockMvc.perform(post("/api/gift-idea-reservations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(giftIdeaReservation)))
            .andExpect(status().isBadRequest());

        // Validate the GiftIdeaReservation in the database
        List<GiftIdeaReservation> giftIdeaReservationList = giftIdeaReservationRepository.findAll();
        assertThat(giftIdeaReservationList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    void checkCreationDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = giftIdeaReservationRepository.findAll().size();
        // set the field null
        giftIdeaReservation.setCreationDate(null);

        // Create the GiftIdeaReservation, which fails.

        restGiftIdeaReservationMockMvc.perform(post("/api/gift-idea-reservations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(giftIdeaReservation)))
            .andExpect(status().isBadRequest());

        List<GiftIdeaReservation> giftIdeaReservationList = giftIdeaReservationRepository.findAll();
        assertThat(giftIdeaReservationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllGiftIdeaReservations() throws Exception {
        // Initialize the database
        giftIdeaReservationRepository.saveAndFlush(giftIdeaReservation);

        // Get all the giftIdeaReservationList
        restGiftIdeaReservationMockMvc.perform(get("/api/gift-idea-reservations?sort=id,desc"))
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @Transactional
    void getGiftIdeaReservation() throws Exception {
        // Initialize the database
        giftIdeaReservationRepository.saveAndFlush(giftIdeaReservation);

        // Get the giftIdeaReservation
        restGiftIdeaReservationMockMvc.perform(get("/api/gift-idea-reservations/{id}", giftIdeaReservation.getId()))
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @Transactional
    void getNonExistingGiftIdeaReservation() throws Exception {
        // Get the giftIdeaReservation
        restGiftIdeaReservationMockMvc.perform(get("/api/gift-idea-reservations/{id}", Long.MAX_VALUE))
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @Transactional
    void updateGiftIdeaReservation() throws Exception {
        // Initialize the database
        giftIdeaReservationService.save(giftIdeaReservation, "alice");

        int databaseSizeBeforeUpdate = giftIdeaReservationRepository.findAll().size();

        // Update the giftIdeaReservation
        GiftIdeaReservation updatedGiftIdeaReservation = giftIdeaReservationRepository.findById(giftIdeaReservation.getId()).get();
        // Disconnect from session so that the updates on updatedGiftIdeaReservation are not directly saved in db
        em.detach(updatedGiftIdeaReservation);
        updatedGiftIdeaReservation
            .creationDate(UPDATED_CREATION_DATE);

        restGiftIdeaReservationMockMvc.perform(put("/api/gift-idea-reservations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedGiftIdeaReservation)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the GiftIdeaReservation in the database
        List<GiftIdeaReservation> giftIdeaReservationList = giftIdeaReservationRepository.findAll();
        assertThat(giftIdeaReservationList).hasSize(databaseSizeBeforeUpdate);
        GiftIdeaReservation testGiftIdeaReservation = giftIdeaReservationList.get(giftIdeaReservationList.size() - 1);
        assertThat(testGiftIdeaReservation.getCreationDate()).isNotEqualTo(UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void updateNonExistingGiftIdeaReservation() throws Exception {
        int databaseSizeBeforeUpdate = giftIdeaReservationRepository.findAll().size();

        // Create the GiftIdeaReservation

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGiftIdeaReservationMockMvc.perform(put("/api/gift-idea-reservations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(giftIdeaReservation)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the GiftIdeaReservation in the database
        List<GiftIdeaReservation> giftIdeaReservationList = giftIdeaReservationRepository.findAll();
        assertThat(giftIdeaReservationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGiftIdeaReservation() throws Exception {
        // Initialize the database
        giftIdeaReservationService.save(giftIdeaReservation, "alice");

        int databaseSizeBeforeDelete = giftIdeaReservationRepository.findAll().size();

        // Delete the giftIdeaReservation
        restGiftIdeaReservationMockMvc.perform(delete("/api/gift-idea-reservations/{id}", giftIdeaReservation.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isUnauthorized());

        // Validate the database contains one less item
        List<GiftIdeaReservation> giftIdeaReservationList = giftIdeaReservationRepository.findAll();
        assertThat(giftIdeaReservationList).hasSize(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GiftIdeaReservation.class);
        GiftIdeaReservation giftIdeaReservation1 = new GiftIdeaReservation();
        giftIdeaReservation1.setId(1L);
        GiftIdeaReservation giftIdeaReservation2 = new GiftIdeaReservation();
        giftIdeaReservation2.setId(giftIdeaReservation1.getId());
        assertThat(giftIdeaReservation1).isEqualTo(giftIdeaReservation2);
        giftIdeaReservation2.setId(2L);
        assertThat(giftIdeaReservation1).isNotEqualTo(giftIdeaReservation2);
        giftIdeaReservation1.setId(null);
        assertThat(giftIdeaReservation1).isNotEqualTo(giftIdeaReservation2);
    }
}
