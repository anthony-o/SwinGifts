package com.github.anthonyo.swingifts.web.rest;

import com.github.anthonyo.swingifts.SwinGiftsApp;
import com.github.anthonyo.swingifts.domain.GiftDrawing;
import com.github.anthonyo.swingifts.repository.GiftDrawingRepository;
import com.github.anthonyo.swingifts.service.GiftDrawingService;
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
import java.util.List;

import static com.github.anthonyo.swingifts.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link GiftDrawingResource} REST controller.
 */
@SpringBootTest(classes = SwinGiftsApp.class)
public class GiftDrawingResourceIT {

    @Autowired
    private GiftDrawingRepository giftDrawingRepository;

    @Autowired
    private GiftDrawingService giftDrawingService;

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

    private MockMvc restGiftDrawingMockMvc;

    private GiftDrawing giftDrawing;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final GiftDrawingResource giftDrawingResource = new GiftDrawingResource(giftDrawingService);
        this.restGiftDrawingMockMvc = MockMvcBuilders.standaloneSetup(giftDrawingResource)
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
    public static GiftDrawing createEntity(EntityManager em) {
        GiftDrawing giftDrawing = new GiftDrawing();
        return giftDrawing;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GiftDrawing createUpdatedEntity(EntityManager em) {
        GiftDrawing giftDrawing = new GiftDrawing();
        return giftDrawing;
    }

    @BeforeEach
    public void initTest() {
        giftDrawing = createEntity(em);
    }

    @Test
    @Transactional
    public void createGiftDrawing() throws Exception {
        int databaseSizeBeforeCreate = giftDrawingRepository.findAll().size();

        // Create the GiftDrawing
        restGiftDrawingMockMvc.perform(post("/api/gift-drawings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(giftDrawing)))
            .andExpect(status().isCreated());

        // Validate the GiftDrawing in the database
        List<GiftDrawing> giftDrawingList = giftDrawingRepository.findAll();
        assertThat(giftDrawingList).hasSize(databaseSizeBeforeCreate + 1);
        GiftDrawing testGiftDrawing = giftDrawingList.get(giftDrawingList.size() - 1);
    }

    @Test
    @Transactional
    public void createGiftDrawingWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = giftDrawingRepository.findAll().size();

        // Create the GiftDrawing with an existing ID
        giftDrawing.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restGiftDrawingMockMvc.perform(post("/api/gift-drawings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(giftDrawing)))
            .andExpect(status().isBadRequest());

        // Validate the GiftDrawing in the database
        List<GiftDrawing> giftDrawingList = giftDrawingRepository.findAll();
        assertThat(giftDrawingList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllGiftDrawings() throws Exception {
        // Initialize the database
        giftDrawingRepository.saveAndFlush(giftDrawing);

        // Get all the giftDrawingList
        restGiftDrawingMockMvc.perform(get("/api/gift-drawings?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(giftDrawing.getId().intValue())));
    }
    
    @Test
    @Transactional
    public void getGiftDrawing() throws Exception {
        // Initialize the database
        giftDrawingRepository.saveAndFlush(giftDrawing);

        // Get the giftDrawing
        restGiftDrawingMockMvc.perform(get("/api/gift-drawings/{id}", giftDrawing.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(giftDrawing.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingGiftDrawing() throws Exception {
        // Get the giftDrawing
        restGiftDrawingMockMvc.perform(get("/api/gift-drawings/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateGiftDrawing() throws Exception {
        // Initialize the database
        giftDrawingService.save(giftDrawing);

        int databaseSizeBeforeUpdate = giftDrawingRepository.findAll().size();

        // Update the giftDrawing
        GiftDrawing updatedGiftDrawing = giftDrawingRepository.findById(giftDrawing.getId()).get();
        // Disconnect from session so that the updates on updatedGiftDrawing are not directly saved in db
        em.detach(updatedGiftDrawing);

        restGiftDrawingMockMvc.perform(put("/api/gift-drawings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedGiftDrawing)))
            .andExpect(status().isOk());

        // Validate the GiftDrawing in the database
        List<GiftDrawing> giftDrawingList = giftDrawingRepository.findAll();
        assertThat(giftDrawingList).hasSize(databaseSizeBeforeUpdate);
        GiftDrawing testGiftDrawing = giftDrawingList.get(giftDrawingList.size() - 1);
    }

    @Test
    @Transactional
    public void updateNonExistingGiftDrawing() throws Exception {
        int databaseSizeBeforeUpdate = giftDrawingRepository.findAll().size();

        // Create the GiftDrawing

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGiftDrawingMockMvc.perform(put("/api/gift-drawings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(giftDrawing)))
            .andExpect(status().isBadRequest());

        // Validate the GiftDrawing in the database
        List<GiftDrawing> giftDrawingList = giftDrawingRepository.findAll();
        assertThat(giftDrawingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteGiftDrawing() throws Exception {
        // Initialize the database
        giftDrawingService.save(giftDrawing);

        int databaseSizeBeforeDelete = giftDrawingRepository.findAll().size();

        // Delete the giftDrawing
        restGiftDrawingMockMvc.perform(delete("/api/gift-drawings/{id}", giftDrawing.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<GiftDrawing> giftDrawingList = giftDrawingRepository.findAll();
        assertThat(giftDrawingList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GiftDrawing.class);
        GiftDrawing giftDrawing1 = new GiftDrawing();
        giftDrawing1.setId(1L);
        GiftDrawing giftDrawing2 = new GiftDrawing();
        giftDrawing2.setId(giftDrawing1.getId());
        assertThat(giftDrawing1).isEqualTo(giftDrawing2);
        giftDrawing2.setId(2L);
        assertThat(giftDrawing1).isNotEqualTo(giftDrawing2);
        giftDrawing1.setId(null);
        assertThat(giftDrawing1).isNotEqualTo(giftDrawing2);
    }
}
