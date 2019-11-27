package com.github.anthonyo.swingifts.service;

import com.github.anthonyo.swingifts.domain.GiftIdea;
import com.github.anthonyo.swingifts.domain.GiftIdeaReservation;
import com.github.anthonyo.swingifts.domain.Participation;
import com.github.anthonyo.swingifts.repository.GiftIdeaRepository;
import com.github.anthonyo.swingifts.repository.GiftIdeaReservationRepository;
import com.github.anthonyo.swingifts.repository.ParticipationRepository;
import com.github.anthonyo.swingifts.service.errors.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link GiftIdeaReservation}.
 */
@Service
@Transactional
public class GiftIdeaReservationService {

    private final Logger log = LoggerFactory.getLogger(GiftIdeaReservationService.class);

    private final GiftIdeaReservationRepository giftIdeaReservationRepository;
    private final GiftIdeaRepository giftIdeaRepository;
    private final ParticipationRepository participationRepository;
    private final ParticipationService participationService;
    private final GiftIdeaService giftIdeaService;

    public GiftIdeaReservationService(GiftIdeaReservationRepository giftIdeaReservationRepository, GiftIdeaRepository giftIdeaRepository, ParticipationRepository participationRepository, ParticipationService participationService, GiftIdeaService giftIdeaService) {
        this.giftIdeaReservationRepository = giftIdeaReservationRepository;
        this.giftIdeaRepository = giftIdeaRepository;
        this.participationRepository = participationRepository;
        this.participationService = participationService;
        this.giftIdeaService = giftIdeaService;
    }

    /**
     * Save a giftIdeaReservation.
     *
     * @param giftIdeaReservation the entity to save.
     * @return the persisted entity.
     */
    public GiftIdeaReservation save(GiftIdeaReservation giftIdeaReservation, String requesterUserLogin) throws EntityNotFoundException, AccessDeniedException {
        log.debug("Request to save GiftIdeaReservation : {}", giftIdeaReservation);

        GiftIdea giftIdea = giftIdeaRepository.findById(giftIdeaReservation.getGiftIdea().getId()).orElseThrow(() -> new EntityNotFoundException("Gift idea not found."));
        giftIdeaReservation.setGiftIdea(giftIdea);

        Participation participation = giftIdeaReservation.getParticipation();
        if (participation != null) {
            participationRepository.findById(participation.getId()).orElseThrow(() -> new EntityNotFoundException("Participation not found."));
        } else {
            // participation not given, try to get the one of the current user
            participationService.checkParticipationIdAllowedForRequesterUserLogin(giftIdea.getRecipient().getId(), requesterUserLogin);
            participation = giftIdeaService.getRequesterParticipationOrThrowAccessDeniedException(giftIdea, requesterUserLogin);
        }
        if (!requesterUserLogin.equals(participation.getUser().getLogin())) {
            throw new AccessDeniedException("User is not the one of the participation");
        }
        giftIdeaReservation.setParticipation(participation);

        if (giftIdeaReservation.getId() == null) {
            giftIdeaReservation.setCreationDate(Instant.now());
        }

        return giftIdeaReservationRepository.save(giftIdeaReservation);
    }


    /**
     * Delete the giftIdeaReservation by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id, String requesterUserLogin) throws EntityNotFoundException, AccessDeniedException {
        log.debug("Request to delete GiftIdeaReservation : {}", id);
        GiftIdeaReservation giftIdeaReservation = giftIdeaReservationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Gift idea reservation not found"));
        if (!requesterUserLogin.equals(giftIdeaReservation.getParticipation().getUser().getLogin())) {
            throw new AccessDeniedException("User is not the one of the participation");
        }
        giftIdeaReservationRepository.deleteById(id);
    }
}
