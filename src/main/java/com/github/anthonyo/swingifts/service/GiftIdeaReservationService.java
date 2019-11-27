package com.github.anthonyo.swingifts.service;

import com.github.anthonyo.swingifts.domain.GiftIdeaReservation;
import com.github.anthonyo.swingifts.repository.GiftIdeaReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public GiftIdeaReservationService(GiftIdeaReservationRepository giftIdeaReservationRepository) {
        this.giftIdeaReservationRepository = giftIdeaReservationRepository;
    }

    /**
     * Save a giftIdeaReservation.
     *
     * @param giftIdeaReservation the entity to save.
     * @return the persisted entity.
     */
    public GiftIdeaReservation save(GiftIdeaReservation giftIdeaReservation) {
        log.debug("Request to save GiftIdeaReservation : {}", giftIdeaReservation);
        return giftIdeaReservationRepository.save(giftIdeaReservation);
    }

    /**
     * Get all the giftIdeaReservations.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<GiftIdeaReservation> findAll() {
        log.debug("Request to get all GiftIdeaReservations");
        return giftIdeaReservationRepository.findAll();
    }


    /**
     * Get one giftIdeaReservation by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<GiftIdeaReservation> findOne(Long id) {
        log.debug("Request to get GiftIdeaReservation : {}", id);
        return giftIdeaReservationRepository.findById(id);
    }

    /**
     * Delete the giftIdeaReservation by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete GiftIdeaReservation : {}", id);
        giftIdeaReservationRepository.deleteById(id);
    }
}
