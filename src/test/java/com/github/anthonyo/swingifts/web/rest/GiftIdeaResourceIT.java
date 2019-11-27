package com.github.anthonyo.swingifts.web.rest;

import com.github.anthonyo.swingifts.SwinGiftsApp;
import com.github.anthonyo.swingifts.domain.GiftIdea;
import com.github.anthonyo.swingifts.domain.Participation;
import com.github.anthonyo.swingifts.repository.GiftIdeaRepository;
import com.github.anthonyo.swingifts.service.GiftIdeaService;
import com.github.anthonyo.swingifts.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
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
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link GiftIdeaResource} REST controller.
 */
@SpringBootTest(classes = SwinGiftsApp.class)
public class GiftIdeaResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATION_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATION_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_MODIFICATION_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_MODIFICATION_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private GiftIdeaRepository giftIdeaRepository;

    @Autowired
    private GiftIdeaService giftIdeaService;

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

    private MockMvc restGiftIdeaMockMvc;

    private GiftIdea giftIdea;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final GiftIdeaResource giftIdeaResource = new GiftIdeaResource(giftIdeaService);
        this.restGiftIdeaMockMvc = MockMvcBuilders.standaloneSetup(giftIdeaResource)
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
    public static GiftIdea createEntity(EntityManager em) {
        GiftIdea giftIdea = new GiftIdea()
            .description(DEFAULT_DESCRIPTION)
            .url(DEFAULT_URL)
            .creationDate(DEFAULT_CREATION_DATE)
            .modificationDate(DEFAULT_MODIFICATION_DATE);
        // Add required entity
        Participation participation;
        if (TestUtil.findAll(em, Participation.class).isEmpty()) {
            participation = ParticipationResourceIT.createEntity(em);
            em.persist(participation);
            em.flush();
        } else {
            participation = TestUtil.findAll(em, Participation.class).get(0);
        }
        giftIdea.setCreator(participation);
        // Add required entity
        giftIdea.setRecipient(participation);
        return giftIdea;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GiftIdea createUpdatedEntity(EntityManager em) {
        GiftIdea giftIdea = new GiftIdea()
            .description(UPDATED_DESCRIPTION)
            .url(UPDATED_URL)
            .creationDate(UPDATED_CREATION_DATE)
            .modificationDate(UPDATED_MODIFICATION_DATE);
        // Add required entity
        Participation participation;
        if (TestUtil.findAll(em, Participation.class).isEmpty()) {
            participation = ParticipationResourceIT.createUpdatedEntity(em);
            em.persist(participation);
            em.flush();
        } else {
            participation = TestUtil.findAll(em, Participation.class).get(0);
        }
        giftIdea.setCreator(participation);
        // Add required entity
        giftIdea.setRecipient(participation);
        return giftIdea;
    }

    @BeforeEach
    public void initTest() {
        giftIdea = createEntity(em);
    }

    @Test
    @Transactional
    public void createGiftIdea() throws Exception {
        int databaseSizeBeforeCreate = giftIdeaRepository.findAll().size();

        // Create the GiftIdea
        restGiftIdeaMockMvc.perform(post("/api/gift-ideas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(giftIdea)))
            .andExpect(status().isCreated());

        // Validate the GiftIdea in the database
        List<GiftIdea> giftIdeaList = giftIdeaRepository.findAll();
        assertThat(giftIdeaList).hasSize(databaseSizeBeforeCreate + 1);
        GiftIdea testGiftIdea = giftIdeaList.get(giftIdeaList.size() - 1);
        assertThat(testGiftIdea.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testGiftIdea.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testGiftIdea.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testGiftIdea.getModificationDate()).isEqualTo(DEFAULT_MODIFICATION_DATE);
    }

    @Test
    @Transactional
    public void createGiftIdeaWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = giftIdeaRepository.findAll().size();

        // Create the GiftIdea with an existing ID
        giftIdea.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restGiftIdeaMockMvc.perform(post("/api/gift-ideas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(giftIdea)))
            .andExpect(status().isBadRequest());

        // Validate the GiftIdea in the database
        List<GiftIdea> giftIdeaList = giftIdeaRepository.findAll();
        assertThat(giftIdeaList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = giftIdeaRepository.findAll().size();
        // set the field null
        giftIdea.setDescription(null);

        // Create the GiftIdea, which fails.

        restGiftIdeaMockMvc.perform(post("/api/gift-ideas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(giftIdea)))
            .andExpect(status().isBadRequest());

        List<GiftIdea> giftIdeaList = giftIdeaRepository.findAll();
        assertThat(giftIdeaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCreationDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = giftIdeaRepository.findAll().size();
        // set the field null
        giftIdea.setCreationDate(null);

        // Create the GiftIdea, which fails.

        restGiftIdeaMockMvc.perform(post("/api/gift-ideas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(giftIdea)))
            .andExpect(status().isBadRequest());

        List<GiftIdea> giftIdeaList = giftIdeaRepository.findAll();
        assertThat(giftIdeaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllGiftIdeas() throws Exception {
        // Initialize the database
        giftIdeaRepository.saveAndFlush(giftIdea);

        // Get all the giftIdeaList
        restGiftIdeaMockMvc.perform(get("/api/gift-ideas?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(giftIdea.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].modificationDate").value(hasItem(DEFAULT_MODIFICATION_DATE.toString())));
    }
    
    @Test
    @Transactional
    public void getGiftIdea() throws Exception {
        // Initialize the database
        giftIdeaRepository.saveAndFlush(giftIdea);

        // Get the giftIdea
        restGiftIdeaMockMvc.perform(get("/api/gift-ideas/{id}", giftIdea.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(giftIdea.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE.toString()))
            .andExpect(jsonPath("$.modificationDate").value(DEFAULT_MODIFICATION_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingGiftIdea() throws Exception {
        // Get the giftIdea
        restGiftIdeaMockMvc.perform(get("/api/gift-ideas/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateGiftIdea() throws Exception {
        // Initialize the database
        giftIdeaService.save(giftIdea);

        int databaseSizeBeforeUpdate = giftIdeaRepository.findAll().size();

        // Update the giftIdea
        GiftIdea updatedGiftIdea = giftIdeaRepository.findById(giftIdea.getId()).get();
        // Disconnect from session so that the updates on updatedGiftIdea are not directly saved in db
        em.detach(updatedGiftIdea);
        updatedGiftIdea
            .description(UPDATED_DESCRIPTION)
            .url(UPDATED_URL)
            .creationDate(UPDATED_CREATION_DATE)
            .modificationDate(UPDATED_MODIFICATION_DATE);

        restGiftIdeaMockMvc.perform(put("/api/gift-ideas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedGiftIdea)))
            .andExpect(status().isOk());

        // Validate the GiftIdea in the database
        List<GiftIdea> giftIdeaList = giftIdeaRepository.findAll();
        assertThat(giftIdeaList).hasSize(databaseSizeBeforeUpdate);
        GiftIdea testGiftIdea = giftIdeaList.get(giftIdeaList.size() - 1);
        assertThat(testGiftIdea.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testGiftIdea.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testGiftIdea.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testGiftIdea.getModificationDate()).isEqualTo(UPDATED_MODIFICATION_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingGiftIdea() throws Exception {
        int databaseSizeBeforeUpdate = giftIdeaRepository.findAll().size();

        // Create the GiftIdea

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGiftIdeaMockMvc.perform(put("/api/gift-ideas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(giftIdea)))
            .andExpect(status().isBadRequest());

        // Validate the GiftIdea in the database
        List<GiftIdea> giftIdeaList = giftIdeaRepository.findAll();
        assertThat(giftIdeaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteGiftIdea() throws Exception {
        // Initialize the database
        giftIdeaService.save(giftIdea);

        int databaseSizeBeforeDelete = giftIdeaRepository.findAll().size();

        // Delete the giftIdea
        restGiftIdeaMockMvc.perform(delete("/api/gift-ideas/{id}", giftIdea.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<GiftIdea> giftIdeaList = giftIdeaRepository.findAll();
        assertThat(giftIdeaList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GiftIdea.class);
        GiftIdea giftIdea1 = new GiftIdea();
        giftIdea1.setId(1L);
        GiftIdea giftIdea2 = new GiftIdea();
        giftIdea2.setId(giftIdea1.getId());
        assertThat(giftIdea1).isEqualTo(giftIdea2);
        giftIdea2.setId(2L);
        assertThat(giftIdea1).isNotEqualTo(giftIdea2);
        giftIdea1.setId(null);
        assertThat(giftIdea1).isNotEqualTo(giftIdea2);
    }
}
