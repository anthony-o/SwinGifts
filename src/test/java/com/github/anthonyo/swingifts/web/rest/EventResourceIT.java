package com.github.anthonyo.swingifts.web.rest;

import com.github.anthonyo.swingifts.SwinGiftsApp;
import com.github.anthonyo.swingifts.domain.Event;
import com.github.anthonyo.swingifts.domain.Participation;
import com.github.anthonyo.swingifts.domain.User;
import com.github.anthonyo.swingifts.repository.EventRepository;
import com.github.anthonyo.swingifts.repository.GiftDrawingRepository;
import com.github.anthonyo.swingifts.repository.UserRepository;
import com.github.anthonyo.swingifts.service.EventService;
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
import java.util.HashSet;
import java.util.List;

import static com.github.anthonyo.swingifts.TestConstants.*;
import static com.github.anthonyo.swingifts.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link EventResource} REST controller.
 */
@SpringBootTest(classes = SwinGiftsApp.class)
public class EventResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_PUBLIC_KEY = "AAAAAAAAAA";
    private static final String UPDATED_PUBLIC_KEY = "BBBBBBBBBB";

    private static final Boolean DEFAULT_PUBLIC_KEY_ENABLED = false;
    private static final Boolean UPDATED_PUBLIC_KEY_ENABLED = true;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventService eventService;

    @Autowired
    private GiftDrawingRepository giftDrawingRepository;

    @Autowired
    private UserRepository userRepository;

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

    private MockMvc restEventMockMvc;

    private Event event;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        final EventResource eventResource = new EventResource(eventService);
        this.restEventMockMvc = MockMvcBuilders.standaloneSetup(eventResource)
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
    public static Event createEntity(EntityManager em) {
        Event event = new Event()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .publicKey(DEFAULT_PUBLIC_KEY)
            .publicKeyEnabled(DEFAULT_PUBLIC_KEY_ENABLED);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        event.setAdmin(user);
        return event;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Event createUpdatedEntity(EntityManager em) {
        Event event = new Event()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .publicKey(UPDATED_PUBLIC_KEY)
            .publicKeyEnabled(UPDATED_PUBLIC_KEY_ENABLED);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        event.setAdmin(user);
        return event;
    }

    @BeforeEach
    void initTest() {
        event = createEntity(em);
    }

    @Test
    @Transactional
    @WithMockUser("alice")
    void createEvent() throws Exception {
        int databaseSizeBeforeCreate = eventRepository.findAll().size();

        Event event = new Event()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .publicKeyEnabled(true);

        // Create the Event
        restEventMockMvc.perform(post("/api/events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(event)))
            .andExpect(status().isCreated());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeCreate + 1);
        Event testEvent = eventList.get(eventList.size() - 1);
        assertThat(testEvent.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testEvent.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testEvent.getPublicKey()).isNotNull();
        assertThat(testEvent.isPublicKeyEnabled()).isTrue();
    }

    @Test
    @Transactional
    void createEventWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = eventRepository.findAll().size();

        // Create the Event with an existing ID
        event.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restEventMockMvc.perform(post("/api/events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(event)))
            .andExpect(status().isBadRequest());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = eventRepository.findAll().size();
        // set the field null
        event.setName(null);

        // Create the Event, which fails.

        restEventMockMvc.perform(post("/api/events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(event)))
            .andExpect(status().isBadRequest());

        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @WithMockUser("alice")
    void getAllEvents() throws Exception {
        // Get all the eventList
        restEventMockMvc.perform(get("/api/events?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasSize(4)))
            .andExpect(jsonPath("$.[*].id", hasItem((int) EVENT_ALICES_EVENT_ID)))
            .andExpect(jsonPath("$.[*].id", hasItem((int) EVENT_BOBS_EVENT_ID)))
            .andExpect(jsonPath("$.[*].id", hasItem((int) EVENT_CHARLOTTES_EVENT_ID)))
            .andExpect(jsonPath("$.[*].id", hasItem((int) EVENT_DAVES_EVENT_ID)))
        ;
    }

    @Test
    @Transactional
    @WithMockUser("bob")
    void getEvent() throws Exception {
        // Get the event
        restEventMockMvc.perform(get("/api/events/{id}", EVENT_ALICES_EVENT_ID))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value((int) EVENT_ALICES_EVENT_ID))
            .andExpect(jsonPath("$.name").value("Alice's event"))
            .andExpect(jsonPath("$.description").isEmpty())
            .andExpect(jsonPath("$.publicKey").isEmpty())
            .andExpect(jsonPath("$.publicKeyEnabled").isEmpty());
    }

    @Test
    @Transactional
    @WithMockUser("alice")
    void getEventAsAdmin() throws Exception {
        // Get the event
        restEventMockMvc.perform(get("/api/events/{id}", EVENT_ALICES_EVENT_ID))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value((int) EVENT_ALICES_EVENT_ID))
            .andExpect(jsonPath("$.name").value("Alice's event"))
            .andExpect(jsonPath("$.description").isEmpty())
            .andExpect(jsonPath("$.publicKey").isEmpty())
            .andExpect(jsonPath("$.publicKeyEnabled").isEmpty());
    }

    @Test
    @Transactional
    @WithMockUser("frank")
    void getEventWithoutRights() throws Exception {
        // Get the event
        restEventMockMvc.perform(get("/api/events/{id}", EVENT_ALICES_EVENT_ID))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithMockUser("alice")
    void getNonExistingEvent() throws Exception {
        // Get the event
        restEventMockMvc.perform(get("/api/events/{id}", Long.MAX_VALUE))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithMockUser("alice")
    void updateEvent() throws Exception {
        int databaseSizeBeforeUpdate = eventRepository.findAll().size();

        // Update the event
        Event updatedEvent = eventRepository.findById(EVENT_ALICES_EVENT_ID).get();
        // Disconnect from session so that the updates on updatedEvent are not directly saved in db
        em.detach(updatedEvent);
        // Remove all the one to many relationships
        updatedEvent.setParticipations(new HashSet<>());
        updatedEvent.setGiftDrawings(new HashSet<>());
        updatedEvent.setDrawingExclusionGroups(new HashSet<>());
        updatedEvent
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .publicKeyEnabled(true);

        restEventMockMvc.perform(put("/api/events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedEvent)))
            .andExpect(status().isOk());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        Event testEvent = eventRepository.findById(EVENT_ALICES_EVENT_ID).get();
        assertThat(testEvent.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEvent.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testEvent.getPublicKey()).isNotNull();
        assertThat(testEvent.isPublicKeyEnabled()).isTrue();
    }

    @Test
    @Transactional
    void updateNonExistingEvent() throws Exception {
        int databaseSizeBeforeUpdate = eventRepository.findAll().size();

        // Create the Event

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventMockMvc.perform(put("/api/events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(event)))
            .andExpect(status().isBadRequest());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    @WithMockUser("alice")
    void deleteEvent() throws Exception {
        // Initialize the database
        event.setAdmin(userRepository.findById(USER_ALICE_ID).get());
        event = eventRepository.save(event);

        int databaseSizeBeforeDelete = eventRepository.findAll().size();

        // Delete the event
        restEventMockMvc.perform(delete("/api/events/{id}", event.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Event.class);
        Event event1 = new Event();
        event1.setId(1L);
        Event event2 = new Event();
        event2.setId(event1.getId());
        assertThat(event1).isEqualTo(event2);
        event2.setId(2L);
        assertThat(event1).isNotEqualTo(event2);
        event1.setId(null);
        assertThat(event1).isNotEqualTo(event2);
    }

    @Test
    @Transactional
    @WithMockUser("alice")
    void drawGiftsTest() throws Exception {
        // GIVEN
        long giftDrawingCountBefore = giftDrawingRepository.count();

        // WHEN
        // Launch the gift drawing
        restEventMockMvc.perform(post("/api/events/{id}/draw-gifts", EVENT_ALICES_EVENT_ID)
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // THEN
        assertThat(giftDrawingRepository.count()).isEqualTo(giftDrawingCountBefore + 5);
        Participation alicesGiftDrawingRecipient = giftDrawingRepository.findAll().stream().filter(giftDrawing -> giftDrawing.getEvent().getId() == EVENT_ALICES_EVENT_ID && giftDrawing.getDonor().getUser().getId().equals(USER_ALICE_ID)).findFirst().get().getRecipient();
        restEventMockMvc.perform(get("/api/events/{id}", EVENT_ALICES_EVENT_ID))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.myGiftDrawings.[*].recipient").value(hasSize(1)))
            .andExpect(jsonPath("$.myGiftDrawings.[*].recipient.id").value(alicesGiftDrawingRecipient.getId().intValue()))
            .andExpect(jsonPath("$.myGiftDrawings.[*].recipient.userAlias").value(alicesGiftDrawingRecipient.getUserAlias()));
    }
}
