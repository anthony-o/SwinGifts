package com.github.anthonyo.swingifts.service;

import com.github.anthonyo.swingifts.domain.GiftIdea;
import com.github.anthonyo.swingifts.domain.Participation;
import com.github.anthonyo.swingifts.repository.GiftIdeaRepository;
import com.github.anthonyo.swingifts.repository.GiftIdeaReservationRepository;
import com.github.anthonyo.swingifts.repository.ParticipationRepository;
import com.github.anthonyo.swingifts.service.errors.EntityNotFoundException;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service Implementation for managing {@link GiftIdea}.
 */
@Service
@Transactional
public class GiftIdeaService {

    private final Logger log = LoggerFactory.getLogger(GiftIdeaService.class);

    public static final String ENTITY_NAME = "giftIdea";

    private final GiftIdeaRepository giftIdeaRepository;
    private final ParticipationService participationService;
    private final ParticipationRepository participationRepository;
    private final GiftIdeaReservationRepository giftIdeaReservationRepository;

    public GiftIdeaService(GiftIdeaRepository giftIdeaRepository, ParticipationService participationService, ParticipationRepository participationRepository, GiftIdeaReservationRepository giftIdeaReservationRepository) {
        this.giftIdeaRepository = giftIdeaRepository;
        this.participationService = participationService;
        this.participationRepository = participationRepository;
        this.giftIdeaReservationRepository = giftIdeaReservationRepository;
    }

    /**
     * Save a giftIdea.
     *
     * @param giftIdea the entity to save.
     * @return the persisted entity.
     */
    public GiftIdea save(GiftIdea giftIdea, String requesterUserLogin) throws EntityNotFoundException {
        log.debug("Request to save GiftIdea : {}", giftIdea);
        // Can't change reservation
        giftIdea.getGiftIdeaReservations().clear();

        if (giftIdea.getId() != null) {
            // Update mode
            giftIdea.setModificationDate(Instant.now());
            // Can't change participants fields
            giftIdeaRepository.findById(giftIdea.getId()).map(dbGiftIdea -> {
                giftIdea.setCreator(dbGiftIdea.getCreator());
                giftIdea.setRecipient(dbGiftIdea.getRecipient());
                return giftIdea;
            }).orElseThrow(() -> new EntityNotFoundException("Gift idea not found"));
        } else {
            // Creation mode
            giftIdea.setCreationDate(Instant.now());
            Participation recipient = participationRepository.findById(giftIdea.getRecipient().getId())
                .orElseThrow(() -> new EntityNotFoundException("Recipient participant not found"));
            giftIdea.setRecipient(recipient);
        }
        Participation requesterParticipation = getRequesterParticipationOrThrowAccessDeniedException(giftIdea, requesterUserLogin);
        if (giftIdea.getId() == null) {
            // Creation mode
            giftIdea.setCreator(requesterParticipation);
        } else {
            // Modification mode: only creator can modify this gift idea
            if (!giftIdea.getCreator().equals(requesterParticipation)) {
                throw new AccessDeniedException("User is not the creator of this gift idea: cannot modify it");
            }
        }

        return giftIdeaRepository.save(giftIdea);
    }

    Participation getRequesterParticipationOrThrowAccessDeniedException(GiftIdea giftIdea, String requesterUserLogin) throws AccessDeniedException {
        return participationRepository.findByEventIdAndUserLogin(giftIdea.getRecipient().getEvent().getId(), requesterUserLogin).orElseThrow(() -> new AccessDeniedException("User is not a participant of this event"));
    }

    /**
     * Get all the giftIdeas.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<GiftIdea> findAll() {
        log.debug("Request to get all GiftIdeas");
        return giftIdeaRepository.findAll();
    }


    /**
     * Get one giftIdea by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<GiftIdea> findOne(Long id, String requesterUserLogin) {
        log.debug("Request to get GiftIdea : {}", id);
        return filterGiftIdeaStream(giftIdeaRepository.findById(id).stream(), requesterUserLogin).findFirst();
    }

    /**
     * Delete the giftIdea by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete GiftIdea : {}", id);
        giftIdeaRepository.deleteById(id);
    }

    static boolean filterRequesterUserLoginNotRecipientOrCreator(GiftIdea giftIdea, String requesterUserLogin) {
        return !requesterUserLogin.equals(giftIdea.getRecipient().getUserLoginIgnoringNull())
            || requesterUserLogin.equals(giftIdea.getCreator().getUserLoginIgnoringNull());
    }

    static Stream<GiftIdea> filterGiftIdeaStream(Stream<GiftIdea> giftIdeaStream, String requesterUserLogin) {
        return giftIdeaStream
            .filter(giftIdea -> filterRequesterUserLoginNotRecipientOrCreator(giftIdea, requesterUserLogin))
            .peek(giftIdea -> {
                // If the requester is not the recipient, load the reservation
                if (!requesterUserLogin.equals(giftIdea.getRecipient().getUserLoginIgnoringNull())) {
                    Hibernate.initialize(giftIdea.getGiftIdeaReservations());
                }
            });
    }

    @Transactional(readOnly = true)
    public List<GiftIdea> findByRecipientIdForRequesterUserLogin(Long participationId, String requesterUserLogin) {
        participationService.checkParticipationIdAllowedForRequesterUserLogin(participationId, requesterUserLogin);
        return filterGiftIdeaStream(giftIdeaRepository.findByRecipientId(participationId).stream(), requesterUserLogin).collect(Collectors.toList()); // Collect now in order to execute the stream (and lazy loading) now
    }
}
