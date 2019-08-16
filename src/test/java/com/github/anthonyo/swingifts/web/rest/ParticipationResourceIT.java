package com.github.anthonyo.swingifts.web.rest;

import com.github.anthonyo.swingifts.SwinGiftsApp;
import com.github.anthonyo.swingifts.domain.Participation;
import com.github.anthonyo.swingifts.domain.User;
import com.github.anthonyo.swingifts.domain.Event;
import com.github.anthonyo.swingifts.repository.ParticipationRepository;
import com.github.anthonyo.swingifts.service.ParticipationService;
import com.github.anthonyo.swingifts.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static com.github.anthonyo.swingifts.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ParticipationResource} REST controller.
 */
@SpringBootTest(classes = SwinGiftsApp.class)
public class ParticipationResourceIT {

    private static final Integer DEFAULT_NB_OF_GIFT_TO_RECEIVE = 0;
    private static final Integer UPDATED_NB_OF_GIFT_TO_RECEIVE = 1;
    private static final Integer SMALLER_NB_OF_GIFT_TO_RECEIVE = 0 - 1;

    private static final Integer DEFAULT_NB_OF_GIFT_TO_DONATE = 0;
    private static final Integer UPDATED_NB_OF_GIFT_TO_DONATE = 1;
    private static final Integer SMALLER_NB_OF_GIFT_TO_DONATE = 0 - 1;

    private static final String DEFAULT_USER_ALIAS = "AAAAAAAAAA";
    private static final String UPDATED_USER_ALIAS = "BBBBBBBBBB";

    @Autowired
    private ParticipationRepository participationRepository;

    @Mock
    private ParticipationRepository participationRepositoryMock;

    @Mock
    private ParticipationService participationServiceMock;

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
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        participation.setUser(user);
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
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        participation.setUser(user);
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
    public void createParticipationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = participationRepository.findAll().size();

        // Create the Participation with an existing ID
        participation.setId(1L);

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
    public void getAllParticipations() throws Exception {
        // Initialize the database
        participationRepository.saveAndFlush(participation);

        // Get all the participationList
        restParticipationMockMvc.perform(get("/api/participations?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(participation.getId().intValue())))
            .andExpect(jsonPath("$.[*].nbOfGiftToReceive").value(hasItem(DEFAULT_NB_OF_GIFT_TO_RECEIVE)))
            .andExpect(jsonPath("$.[*].nbOfGiftToDonate").value(hasItem(DEFAULT_NB_OF_GIFT_TO_DONATE)))
            .andExpect(jsonPath("$.[*].userAlias").value(hasItem(DEFAULT_USER_ALIAS.toString())));
    }
    
    @SuppressWarnings({"unchecked"})
    public void getAllParticipationsWithEagerRelationshipsIsEnabled() throws Exception {
        ParticipationResource participationResource = new ParticipationResource(participationServiceMock);
        when(participationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        MockMvc restParticipationMockMvc = MockMvcBuilders.standaloneSetup(participationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restParticipationMockMvc.perform(get("/api/participations?eagerload=true"))
        .andExpect(status().isOk());

        verify(participationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllParticipationsWithEagerRelationshipsIsNotEnabled() throws Exception {
        ParticipationResource participationResource = new ParticipationResource(participationServiceMock);
            when(participationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));
            MockMvc restParticipationMockMvc = MockMvcBuilders.standaloneSetup(participationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restParticipationMockMvc.perform(get("/api/participations?eagerload=true"))
        .andExpect(status().isOk());

            verify(participationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getParticipation() throws Exception {
        // Initialize the database
        participationRepository.saveAndFlush(participation);

        // Get the participation
        restParticipationMockMvc.perform(get("/api/participations/{id}", participation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(participation.getId().intValue()))
            .andExpect(jsonPath("$.nbOfGiftToReceive").value(DEFAULT_NB_OF_GIFT_TO_RECEIVE))
            .andExpect(jsonPath("$.nbOfGiftToDonate").value(DEFAULT_NB_OF_GIFT_TO_DONATE))
            .andExpect(jsonPath("$.userAlias").value(DEFAULT_USER_ALIAS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingParticipation() throws Exception {
        // Get the participation
        restParticipationMockMvc.perform(get("/api/participations/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateParticipation() throws Exception {
        // Initialize the database
        participationService.save(participation);

        int databaseSizeBeforeUpdate = participationRepository.findAll().size();

        // Update the participation
        Participation updatedParticipation = participationRepository.findById(participation.getId()).get();
        // Disconnect from session so that the updates on updatedParticipation are not directly saved in db
        em.detach(updatedParticipation);
        updatedParticipation
            .nbOfGiftToReceive(UPDATED_NB_OF_GIFT_TO_RECEIVE)
            .nbOfGiftToDonate(UPDATED_NB_OF_GIFT_TO_DONATE)
            .userAlias(UPDATED_USER_ALIAS);

        restParticipationMockMvc.perform(put("/api/participations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedParticipation)))
            .andExpect(status().isOk());

        // Validate the Participation in the database
        List<Participation> participationList = participationRepository.findAll();
        assertThat(participationList).hasSize(databaseSizeBeforeUpdate);
        Participation testParticipation = participationList.get(participationList.size() - 1);
        assertThat(testParticipation.getNbOfGiftToReceive()).isEqualTo(UPDATED_NB_OF_GIFT_TO_RECEIVE);
        assertThat(testParticipation.getNbOfGiftToDonate()).isEqualTo(UPDATED_NB_OF_GIFT_TO_DONATE);
        assertThat(testParticipation.getUserAlias()).isEqualTo(UPDATED_USER_ALIAS);
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
    public void deleteParticipation() throws Exception {
        // Initialize the database
        participationService.save(participation);

        int databaseSizeBeforeDelete = participationRepository.findAll().size();

        // Delete the participation
        restParticipationMockMvc.perform(delete("/api/participations/{id}", participation.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Participation> participationList = participationRepository.findAll();
        assertThat(participationList).hasSize(databaseSizeBeforeDelete - 1);
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
}
