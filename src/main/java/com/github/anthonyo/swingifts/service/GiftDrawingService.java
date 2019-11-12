package com.github.anthonyo.swingifts.service;

import com.github.anthonyo.swingifts.domain.GiftDrawing;
import com.github.anthonyo.swingifts.repository.GiftDrawingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link GiftDrawing}.
 */
@Service
@Transactional
public class GiftDrawingService {

    private final Logger log = LoggerFactory.getLogger(GiftDrawingService.class);

    private final GiftDrawingRepository giftDrawingRepository;

    public GiftDrawingService(GiftDrawingRepository giftDrawingRepository) {
        this.giftDrawingRepository = giftDrawingRepository;
    }

    /**
     * Save a giftDrawing.
     *
     * @param giftDrawing the entity to save.
     * @return the persisted entity.
     */
    public GiftDrawing save(GiftDrawing giftDrawing) {
        log.debug("Request to save GiftDrawing : {}", giftDrawing);
        return giftDrawingRepository.save(giftDrawing);
    }

    /**
     * Get all the giftDrawings.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<GiftDrawing> findAll() {
        log.debug("Request to get all GiftDrawings");
        return giftDrawingRepository.findAll();
    }


    /**
     * Get one giftDrawing by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<GiftDrawing> findOne(Long id) {
        log.debug("Request to get GiftDrawing : {}", id);
        return giftDrawingRepository.findById(id);
    }

    /**
     * Delete the giftDrawing by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete GiftDrawing : {}", id);
        giftDrawingRepository.deleteById(id);
    }
}
