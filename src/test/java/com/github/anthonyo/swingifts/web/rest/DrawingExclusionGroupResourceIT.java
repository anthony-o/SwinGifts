package com.github.anthonyo.swingifts.web.rest;

import com.github.anthonyo.swingifts.SwinGiftsApp;
import com.github.anthonyo.swingifts.domain.DrawingExclusionGroup;
import com.github.anthonyo.swingifts.domain.Event;
import com.github.anthonyo.swingifts.repository.DrawingExclusionGroupRepository;
import com.github.anthonyo.swingifts.service.DrawingExclusionGroupService;
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
 * Integration tests for the {@link DrawingExclusionGroupResource} REST controller.
 */
@SpringBootTest(classes = SwinGiftsApp.class)
public class DrawingExclusionGroupResourceIT {

    @Autowired
    private DrawingExclusionGroupRepository drawingExclusionGroupRepository;

    @Mock
    private DrawingExclusionGroupRepository drawingExclusionGroupRepositoryMock;

    @Mock
    private DrawingExclusionGroupService drawingExclusionGroupServiceMock;

    @Autowired
    private DrawingExclusionGroupService drawingExclusionGroupService;

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

    private MockMvc restDrawingExclusionGroupMockMvc;

    private DrawingExclusionGroup drawingExclusionGroup;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final DrawingExclusionGroupResource drawingExclusionGroupResource = new DrawingExclusionGroupResource(drawingExclusionGroupService);
        this.restDrawingExclusionGroupMockMvc = MockMvcBuilders.standaloneSetup(drawingExclusionGroupResource)
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
    public static DrawingExclusionGroup createEntity(EntityManager em) {
        DrawingExclusionGroup drawingExclusionGroup = new DrawingExclusionGroup();
        // Add required entity
        Event event;
        if (TestUtil.findAll(em, Event.class).isEmpty()) {
            event = EventResourceIT.createEntity(em);
            em.persist(event);
            em.flush();
        } else {
            event = TestUtil.findAll(em, Event.class).get(0);
        }
        drawingExclusionGroup.setEvent(event);
        return drawingExclusionGroup;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DrawingExclusionGroup createUpdatedEntity(EntityManager em) {
        DrawingExclusionGroup drawingExclusionGroup = new DrawingExclusionGroup();
        // Add required entity
        Event event;
        if (TestUtil.findAll(em, Event.class).isEmpty()) {
            event = EventResourceIT.createUpdatedEntity(em);
            em.persist(event);
            em.flush();
        } else {
            event = TestUtil.findAll(em, Event.class).get(0);
        }
        drawingExclusionGroup.setEvent(event);
        return drawingExclusionGroup;
    }

    @BeforeEach
    public void initTest() {
        drawingExclusionGroup = createEntity(em);
    }

    @Test
    @Transactional
    public void createDrawingExclusionGroup() throws Exception {
        int databaseSizeBeforeCreate = drawingExclusionGroupRepository.findAll().size();

        // Create the DrawingExclusionGroup
        restDrawingExclusionGroupMockMvc.perform(post("/api/drawing-exclusion-groups")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(drawingExclusionGroup)))
            .andExpect(status().isCreated());

        // Validate the DrawingExclusionGroup in the database
        List<DrawingExclusionGroup> drawingExclusionGroupList = drawingExclusionGroupRepository.findAll();
        assertThat(drawingExclusionGroupList).hasSize(databaseSizeBeforeCreate + 1);
        DrawingExclusionGroup testDrawingExclusionGroup = drawingExclusionGroupList.get(drawingExclusionGroupList.size() - 1);
    }

    @Test
    @Transactional
    public void createDrawingExclusionGroupWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = drawingExclusionGroupRepository.findAll().size();

        // Create the DrawingExclusionGroup with an existing ID
        drawingExclusionGroup.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDrawingExclusionGroupMockMvc.perform(post("/api/drawing-exclusion-groups")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(drawingExclusionGroup)))
            .andExpect(status().isBadRequest());

        // Validate the DrawingExclusionGroup in the database
        List<DrawingExclusionGroup> drawingExclusionGroupList = drawingExclusionGroupRepository.findAll();
        assertThat(drawingExclusionGroupList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllDrawingExclusionGroups() throws Exception {
        // Initialize the database
        drawingExclusionGroupRepository.saveAndFlush(drawingExclusionGroup);

        // Get all the drawingExclusionGroupList
        restDrawingExclusionGroupMockMvc.perform(get("/api/drawing-exclusion-groups?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(drawingExclusionGroup.getId().intValue())));
    }
    
    @SuppressWarnings({"unchecked"})
    public void getAllDrawingExclusionGroupsWithEagerRelationshipsIsEnabled() throws Exception {
        DrawingExclusionGroupResource drawingExclusionGroupResource = new DrawingExclusionGroupResource(drawingExclusionGroupServiceMock);
        when(drawingExclusionGroupServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        MockMvc restDrawingExclusionGroupMockMvc = MockMvcBuilders.standaloneSetup(drawingExclusionGroupResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restDrawingExclusionGroupMockMvc.perform(get("/api/drawing-exclusion-groups?eagerload=true"))
        .andExpect(status().isOk());

        verify(drawingExclusionGroupServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllDrawingExclusionGroupsWithEagerRelationshipsIsNotEnabled() throws Exception {
        DrawingExclusionGroupResource drawingExclusionGroupResource = new DrawingExclusionGroupResource(drawingExclusionGroupServiceMock);
            when(drawingExclusionGroupServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));
            MockMvc restDrawingExclusionGroupMockMvc = MockMvcBuilders.standaloneSetup(drawingExclusionGroupResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restDrawingExclusionGroupMockMvc.perform(get("/api/drawing-exclusion-groups?eagerload=true"))
        .andExpect(status().isOk());

            verify(drawingExclusionGroupServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getDrawingExclusionGroup() throws Exception {
        // Initialize the database
        drawingExclusionGroupRepository.saveAndFlush(drawingExclusionGroup);

        // Get the drawingExclusionGroup
        restDrawingExclusionGroupMockMvc.perform(get("/api/drawing-exclusion-groups/{id}", drawingExclusionGroup.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(drawingExclusionGroup.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingDrawingExclusionGroup() throws Exception {
        // Get the drawingExclusionGroup
        restDrawingExclusionGroupMockMvc.perform(get("/api/drawing-exclusion-groups/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDrawingExclusionGroup() throws Exception {
        // Initialize the database
        drawingExclusionGroupService.save(drawingExclusionGroup);

        int databaseSizeBeforeUpdate = drawingExclusionGroupRepository.findAll().size();

        // Update the drawingExclusionGroup
        DrawingExclusionGroup updatedDrawingExclusionGroup = drawingExclusionGroupRepository.findById(drawingExclusionGroup.getId()).get();
        // Disconnect from session so that the updates on updatedDrawingExclusionGroup are not directly saved in db
        em.detach(updatedDrawingExclusionGroup);

        restDrawingExclusionGroupMockMvc.perform(put("/api/drawing-exclusion-groups")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedDrawingExclusionGroup)))
            .andExpect(status().isOk());

        // Validate the DrawingExclusionGroup in the database
        List<DrawingExclusionGroup> drawingExclusionGroupList = drawingExclusionGroupRepository.findAll();
        assertThat(drawingExclusionGroupList).hasSize(databaseSizeBeforeUpdate);
        DrawingExclusionGroup testDrawingExclusionGroup = drawingExclusionGroupList.get(drawingExclusionGroupList.size() - 1);
    }

    @Test
    @Transactional
    public void updateNonExistingDrawingExclusionGroup() throws Exception {
        int databaseSizeBeforeUpdate = drawingExclusionGroupRepository.findAll().size();

        // Create the DrawingExclusionGroup

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDrawingExclusionGroupMockMvc.perform(put("/api/drawing-exclusion-groups")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(drawingExclusionGroup)))
            .andExpect(status().isBadRequest());

        // Validate the DrawingExclusionGroup in the database
        List<DrawingExclusionGroup> drawingExclusionGroupList = drawingExclusionGroupRepository.findAll();
        assertThat(drawingExclusionGroupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteDrawingExclusionGroup() throws Exception {
        // Initialize the database
        drawingExclusionGroupService.save(drawingExclusionGroup);

        int databaseSizeBeforeDelete = drawingExclusionGroupRepository.findAll().size();

        // Delete the drawingExclusionGroup
        restDrawingExclusionGroupMockMvc.perform(delete("/api/drawing-exclusion-groups/{id}", drawingExclusionGroup.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<DrawingExclusionGroup> drawingExclusionGroupList = drawingExclusionGroupRepository.findAll();
        assertThat(drawingExclusionGroupList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DrawingExclusionGroup.class);
        DrawingExclusionGroup drawingExclusionGroup1 = new DrawingExclusionGroup();
        drawingExclusionGroup1.setId(1L);
        DrawingExclusionGroup drawingExclusionGroup2 = new DrawingExclusionGroup();
        drawingExclusionGroup2.setId(drawingExclusionGroup1.getId());
        assertThat(drawingExclusionGroup1).isEqualTo(drawingExclusionGroup2);
        drawingExclusionGroup2.setId(2L);
        assertThat(drawingExclusionGroup1).isNotEqualTo(drawingExclusionGroup2);
        drawingExclusionGroup1.setId(null);
        assertThat(drawingExclusionGroup1).isNotEqualTo(drawingExclusionGroup2);
    }
}
