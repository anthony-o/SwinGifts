package com.github.anthonyo.swingifts.service;

import com.github.anthonyo.swingifts.domain.Participation;
import com.github.anthonyo.swingifts.repository.ParticipationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Participation}.
 */
@Service
@Transactional
public class ParticipationService {

    private final Logger log = LoggerFactory.getLogger(ParticipationService.class);

    private final ParticipationRepository participationRepository;

    private final EventService eventService;

    public ParticipationService(ParticipationRepository participationRepository, EventService eventService) {
        this.participationRepository = participationRepository;
        this.eventService = eventService;
    }

    /**
     * Save a participation.
     *
     * @param participation the entity to save.
     * @return the persisted entity.
     */
    public Participation save(Participation participation) {
        log.debug("Request to save Participation : {}", participation);
        return participationRepository.save(participation);
    }

    /**
     * Get all the participations.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Participation> findByEventId(Long eventId, String requesterUserLogin) {
        log.debug("Request to get all Participations");
        eventService.checkEventIdAllowedForRequesterUserLogin(eventId, requesterUserLogin);
        return participationRepository.findByEventId(eventId);
    }

    /**
     * Get one participation by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Participation> findOne(Long id) {
        log.debug("Request to get Participation : {}", id);
        return participationRepository.findById(id);
    }

    /**
     * Delete the participation by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Participation : {}", id);
        participationRepository.deleteById(id);
    }

    public void checkParticipationIdAllowedForRequesterUserLogin(Long participationId, String requesterUserLogin) {
        if (!participationRepository.existsByIdAndEventParticipationsUserLoginOrEventAdminLogin(participationId, requesterUserLogin)) {
            throw new AccessDeniedException("User is not allowed to access this participation");
        }
    }
}
