package com.github.anthonyo.swingifts.service;

import com.github.anthonyo.swingifts.domain.Participation;
import com.github.anthonyo.swingifts.repository.ParticipationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public ParticipationService(ParticipationRepository participationRepository) {
        this.participationRepository = participationRepository;
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
    public List<Participation> findAll() {
        log.debug("Request to get all Participations");
        return participationRepository.findAllWithEagerRelationships();
    }

    /**
     * Get all the participations with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Participation> findAllWithEagerRelationships(Pageable pageable) {
        return participationRepository.findAllWithEagerRelationships(pageable);
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
        return participationRepository.findOneWithEagerRelationships(id);
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
}
