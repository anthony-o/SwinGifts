package com.github.anthonyo.swingifts.web.rest;

import com.github.anthonyo.swingifts.domain.DrawingExclusionGroup;
import com.github.anthonyo.swingifts.service.DrawingExclusionGroupService;
import com.github.anthonyo.swingifts.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.github.anthonyo.swingifts.domain.DrawingExclusionGroup}.
 */
@RestController
@RequestMapping("/api")
public class DrawingExclusionGroupResource {

    private final Logger log = LoggerFactory.getLogger(DrawingExclusionGroupResource.class);

    private static final String ENTITY_NAME = "drawingExclusionGroup";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DrawingExclusionGroupService drawingExclusionGroupService;

    public DrawingExclusionGroupResource(DrawingExclusionGroupService drawingExclusionGroupService) {
        this.drawingExclusionGroupService = drawingExclusionGroupService;
    }

    /**
     * {@code POST  /drawing-exclusion-groups} : Create a new drawingExclusionGroup.
     *
     * @param drawingExclusionGroup the drawingExclusionGroup to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new drawingExclusionGroup, or with status {@code 400 (Bad Request)} if the drawingExclusionGroup has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/drawing-exclusion-groups")
    public ResponseEntity<DrawingExclusionGroup> createDrawingExclusionGroup(@Valid @RequestBody DrawingExclusionGroup drawingExclusionGroup) throws URISyntaxException {
        log.debug("REST request to save DrawingExclusionGroup : {}", drawingExclusionGroup);
        if (drawingExclusionGroup.getId() != null) {
            throw new BadRequestAlertException("A new drawingExclusionGroup cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DrawingExclusionGroup result = drawingExclusionGroupService.save(drawingExclusionGroup);
        return ResponseEntity.created(new URI("/api/drawing-exclusion-groups/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /drawing-exclusion-groups} : Updates an existing drawingExclusionGroup.
     *
     * @param drawingExclusionGroup the drawingExclusionGroup to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated drawingExclusionGroup,
     * or with status {@code 400 (Bad Request)} if the drawingExclusionGroup is not valid,
     * or with status {@code 500 (Internal Server Error)} if the drawingExclusionGroup couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/drawing-exclusion-groups")
    public ResponseEntity<DrawingExclusionGroup> updateDrawingExclusionGroup(@Valid @RequestBody DrawingExclusionGroup drawingExclusionGroup) throws URISyntaxException {
        log.debug("REST request to update DrawingExclusionGroup : {}", drawingExclusionGroup);
        if (drawingExclusionGroup.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        DrawingExclusionGroup result = drawingExclusionGroupService.save(drawingExclusionGroup);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, drawingExclusionGroup.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /drawing-exclusion-groups} : get all the drawingExclusionGroups.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of drawingExclusionGroups in body.
     */
    @GetMapping("/drawing-exclusion-groups")
    public List<DrawingExclusionGroup> getAllDrawingExclusionGroups(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all DrawingExclusionGroups");
        return drawingExclusionGroupService.findAll();
    }

    /**
     * {@code GET  /drawing-exclusion-groups/:id} : get the "id" drawingExclusionGroup.
     *
     * @param id the id of the drawingExclusionGroup to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the drawingExclusionGroup, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/drawing-exclusion-groups/{id}")
    public ResponseEntity<DrawingExclusionGroup> getDrawingExclusionGroup(@PathVariable Long id) {
        log.debug("REST request to get DrawingExclusionGroup : {}", id);
        Optional<DrawingExclusionGroup> drawingExclusionGroup = drawingExclusionGroupService.findOne(id);
        return ResponseUtil.wrapOrNotFound(drawingExclusionGroup);
    }

    /**
     * {@code DELETE  /drawing-exclusion-groups/:id} : delete the "id" drawingExclusionGroup.
     *
     * @param id the id of the drawingExclusionGroup to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/drawing-exclusion-groups/{id}")
    public ResponseEntity<Void> deleteDrawingExclusionGroup(@PathVariable Long id) {
        log.debug("REST request to delete DrawingExclusionGroup : {}", id);
        drawingExclusionGroupService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
