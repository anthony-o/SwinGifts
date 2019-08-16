package com.github.anthonyo.swingifts.service;

import com.github.anthonyo.swingifts.domain.GiftIdea;
import com.github.anthonyo.swingifts.repository.GiftIdeaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link GiftIdea}.
 */
@Service
@Transactional
public class GiftIdeaService {

    private final Logger log = LoggerFactory.getLogger(GiftIdeaService.class);

    private final GiftIdeaRepository giftIdeaRepository;

    public GiftIdeaService(GiftIdeaRepository giftIdeaRepository) {
        this.giftIdeaRepository = giftIdeaRepository;
    }

    /**
     * Save a giftIdea.
     *
     * @param giftIdea the entity to save.
     * @return the persisted entity.
     */
    public GiftIdea save(GiftIdea giftIdea) {
        log.debug("Request to save GiftIdea : {}", giftIdea);
        return giftIdeaRepository.save(giftIdea);
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
    public Optional<GiftIdea> findOne(Long id) {
        log.debug("Request to get GiftIdea : {}", id);
        return giftIdeaRepository.findById(id);
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
}
