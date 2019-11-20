package com.github.anthonyo.swingifts.service;

import com.github.anthonyo.swingifts.domain.Event;
import com.github.anthonyo.swingifts.domain.Participation;
import com.github.anthonyo.swingifts.domain.User;
import com.github.anthonyo.swingifts.repository.ParticipationRepository;
import com.github.anthonyo.swingifts.repository.UserRepository;
import com.github.anthonyo.swingifts.service.errors.EntityNotFoundException;
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

    private final UserRepository userRepository;

    public ParticipationService(ParticipationRepository participationRepository, EventService eventService, UserRepository userRepository) {
        this.participationRepository = participationRepository;
        this.eventService = eventService;
        this.userRepository = userRepository;
    }

    /**
     * Save a participation.
     *
     * @param participation the entity to save.
     * @return the persisted entity.
     */
    public Participation save(Participation participation, String requesterUserLogin) throws EntityNotFoundException {
        log.debug("Request to save Participation : {}", participation);
        if (participation.getId() != null) {
            checkParticipationCanBeModifiedByRequesterUserLogin(participation.getId(), requesterUserLogin);
            // When updating a participation, forbid to modify its event & user
            participationRepository.findById(participation.getId()).map(dbParticipation -> {
                participation.setEvent(dbParticipation.getEvent());
                participation.setUser(dbParticipation.getUser());
                return participation;
            }).orElseThrow(() -> new EntityNotFoundException("Participation not found"));
        } else {
            // Load event & user if creating a new participation
            Event event = participation.getEvent();
            if (event != null && event.getId() != null) {
                // Only participant in the event can create a new participant
                eventService.checkEventIdAllowedForRequesterUserLogin(event.getId(), requesterUserLogin);
                eventService.findOneForRequesterUserLogin(event.getId(), requesterUserLogin)
                    .ifPresent(dbEvent -> participation.setEvent(dbEvent));
            }
            User user = participation.getUser();
            if (user != null) {
                if (user.getId() != null) {
                    userRepository.findById(user.getId())
                        .ifPresent(dbUser -> participation.setUser(dbUser));
                } else {
                    if (user.getLogin() != null) {
                        userRepository.findOneByLogin(user.getLogin().toLowerCase())
                            .ifPresent(dbUser -> participation.setUser(dbUser));
                    }
                    if (user.getEmail() != null && (participation.getUser() == null || participation.getUser().getId() == null)) {
                        // An email has been defined and the user was still not found, let's try by email
                        userRepository.findOneByEmailIgnoreCase(user.getEmail())
                            .ifPresent(dbUser -> participation.setUser(dbUser));
                    }
                }
            }
            if (participation.getUser() == null || participation.getUser().getId() == null) {
                // User still not found, let's set it to null
                participation.setUser(null);
            }
        }
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

    public void checkParticipationCanBeModifiedByRequesterUserLogin(Long participationId, String requesterUserLogin) {
        // Only event's admin or participant's user can update this event
        if (!participationRepository.existsByIdAndUserLoginOrEventAdminLogin(participationId, requesterUserLogin)) {
            throw new AccessDeniedException("Only event's admin or participation user can modify this participation");
        }
    }
}
