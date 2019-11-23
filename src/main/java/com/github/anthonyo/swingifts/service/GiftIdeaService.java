package com.github.anthonyo.swingifts.service;

import com.github.anthonyo.swingifts.domain.GiftIdea;
import com.github.anthonyo.swingifts.domain.Participation;
import com.github.anthonyo.swingifts.repository.GiftIdeaRepository;
import com.github.anthonyo.swingifts.repository.ParticipationRepository;
import com.github.anthonyo.swingifts.service.errors.EntityNotFoundException;
import com.github.anthonyo.swingifts.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
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

    public GiftIdeaService(GiftIdeaRepository giftIdeaRepository, ParticipationService participationService, ParticipationRepository participationRepository) {
        this.giftIdeaRepository = giftIdeaRepository;
        this.participationService = participationService;
        this.participationRepository = participationRepository;
    }

    /**
     * Save a giftIdea.
     *
     * @param giftIdea the entity to save.
     * @return the persisted entity.
     */
    public GiftIdea save(GiftIdea giftIdea, String requesterUserLogin) throws EntityNotFoundException {
        log.debug("Request to save GiftIdea : {}", giftIdea);
        if (giftIdea.getId() != null) {
            // Update mode
            giftIdea.setModificationDate(Instant.now());
            // Can't change participants fields
            giftIdeaRepository.findById(giftIdea.getId()).map(dbGiftIdea -> {
                giftIdea.setCreator(dbGiftIdea.getCreator());
                giftIdea.setRecipient(dbGiftIdea.getRecipient());
                Participation dbTaker = dbGiftIdea.getTaker();
                if (dbTaker != null) {
                    // If taker is already present, can't change it
                    giftIdea.setTaker(dbTaker);
                }
                giftIdea.setTakenDate(dbGiftIdea.getTakenDate());
                return giftIdea;
            }).orElseThrow(() -> new EntityNotFoundException("Gift idea not found"));
        } else {
            // Creation mode
            giftIdea.setCreationDate(Instant.now());
            Participation recipient = participationRepository.findById(giftIdea.getRecipient().getId())
                .orElseThrow(() -> new EntityNotFoundException("Recipient participant not found"));
            giftIdea.setRecipient(recipient);
            giftIdea.setTaker(null); // No taker when creating
            giftIdea.setTakenDate(null);
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

    private Participation getRequesterParticipationOrThrowAccessDeniedException(GiftIdea giftIdea, String requesterUserLogin) throws AccessDeniedException {
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
                // If the requester is the recipient, hide the taker
                if (requesterUserLogin.equals(giftIdea.getRecipient().getUserLoginIgnoringNull())) {
                    giftIdea.setTaker(null);
                }
            });
    }

    @Transactional(readOnly = true)
    public Stream<GiftIdea> findByRecipientIdForRequesterUserLogin(Long participationId, String requesterUserLogin) {
        participationService.checkParticipationIdAllowedForRequesterUserLogin(participationId, requesterUserLogin);
        return filterGiftIdeaStream(giftIdeaRepository.findByRecipientId(participationId).stream(), requesterUserLogin);
    }

    public GiftIdea takeById(Long id, String requesterUserLogin) throws EntityNotFoundException {
        GiftIdea giftIdea = giftIdeaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Gift idea not found."));
        if (giftIdea.getTaker() != null) {
            throw new BadRequestAlertException("This gift idea is already taken by someone else", ENTITY_NAME, "takerexists");
        }
        participationService.checkParticipationIdAllowedForRequesterUserLogin(giftIdea.getRecipient().getId(), requesterUserLogin);
        Participation requesterParticipation = getRequesterParticipationOrThrowAccessDeniedException(giftIdea, requesterUserLogin);
        giftIdea.setTaker(requesterParticipation);
        giftIdea.setTakenDate(Instant.now());
        return giftIdeaRepository.save(giftIdea);
    }

    public GiftIdea releaseById(Long id, String requesterUserLogin) throws EntityNotFoundException {
        GiftIdea giftIdea = giftIdeaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Gift idea not found."));
        Participation requesterParticipation = getRequesterParticipationOrThrowAccessDeniedException(giftIdea, requesterUserLogin);
        if (!requesterParticipation.equals(giftIdea.getTaker())) {
            throw new BadRequestAlertException("This gift idea is not taken by the current user", ENTITY_NAME, "takerisnotcurrentuser");
        }
        giftIdea.setTaker(null);
        giftIdea.setTakenDate(null);
        return giftIdeaRepository.save(giftIdea);
    }
}
