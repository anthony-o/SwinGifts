package com.github.anthonyo.swingifts.service;

import com.github.anthonyo.swingifts.domain.Event;
import com.github.anthonyo.swingifts.domain.Participation;
import com.github.anthonyo.swingifts.repository.EventRepository;
import com.github.anthonyo.swingifts.security.AuthoritiesConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Event}.
 */
@Service
@Transactional
public class EventService {

    private final Logger log = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepository;

    private final EntityManager entityManager;

    public EventService(EventRepository eventRepository, EntityManager entityManager) {
        this.eventRepository = eventRepository;
        this.entityManager = entityManager;
    }

    /**
     * Save a event.
     *
     * @param event the entity to save.
     * @return the persisted entity.
     */
    public Event save(Event event) {
        log.debug("Request to save Event : {}", event);
        return eventRepository.save(event);
    }

    /**
     * Get all the events.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Event> findAll() {
        log.debug("Request to get all Events");
        return eventRepository.findAll();
    }


    /**
     * Get one event by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Event> findOne(Long id) {
        log.debug("Request to get Event : {}", id);
        return eventRepository.findById(id);
    }

    /**
     * Delete the event by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Event : {}", id);
        eventRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Event> findOneWithEagerRelationships(Long id, String requesterUserLogin) {
        return eventRepository.findById(id).map(event -> {
            for (Participation participation : event.getParticipations()) {
                // Filter gift ideas : must be either the ones of the requester or not concerning the requester
                participation.setGiftIdeas(
                    participation.getGiftIdeas().stream()
                        .filter(giftIdea ->
                            !giftIdea.getRecipient().getUser().getLogin().equals(requesterUserLogin)
                            || giftIdea.getCreator().getUser().getLogin().equals(requesterUserLogin)
                        ).collect(Collectors.toUnmodifiableSet())
                );
            }
            entityManager.detach(event);
            return event;
        });
    }
}
