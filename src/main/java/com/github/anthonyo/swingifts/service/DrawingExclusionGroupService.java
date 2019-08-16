package com.github.anthonyo.swingifts.service;

import com.github.anthonyo.swingifts.domain.DrawingExclusionGroup;
import com.github.anthonyo.swingifts.repository.DrawingExclusionGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link DrawingExclusionGroup}.
 */
@Service
@Transactional
public class DrawingExclusionGroupService {

    private final Logger log = LoggerFactory.getLogger(DrawingExclusionGroupService.class);

    private final DrawingExclusionGroupRepository drawingExclusionGroupRepository;

    public DrawingExclusionGroupService(DrawingExclusionGroupRepository drawingExclusionGroupRepository) {
        this.drawingExclusionGroupRepository = drawingExclusionGroupRepository;
    }

    /**
     * Save a drawingExclusionGroup.
     *
     * @param drawingExclusionGroup the entity to save.
     * @return the persisted entity.
     */
    public DrawingExclusionGroup save(DrawingExclusionGroup drawingExclusionGroup) {
        log.debug("Request to save DrawingExclusionGroup : {}", drawingExclusionGroup);
        return drawingExclusionGroupRepository.save(drawingExclusionGroup);
    }

    /**
     * Get all the drawingExclusionGroups.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<DrawingExclusionGroup> findAll() {
        log.debug("Request to get all DrawingExclusionGroups");
        return drawingExclusionGroupRepository.findAllWithEagerRelationships();
    }

    /**
     * Get all the drawingExclusionGroups with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<DrawingExclusionGroup> findAllWithEagerRelationships(Pageable pageable) {
        return drawingExclusionGroupRepository.findAllWithEagerRelationships(pageable);
    }
    

    /**
     * Get one drawingExclusionGroup by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<DrawingExclusionGroup> findOne(Long id) {
        log.debug("Request to get DrawingExclusionGroup : {}", id);
        return drawingExclusionGroupRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the drawingExclusionGroup by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete DrawingExclusionGroup : {}", id);
        drawingExclusionGroupRepository.deleteById(id);
    }
}
