package com.github.anthonyo.swingifts.web.rest;

import com.github.anthonyo.swingifts.domain.GiftDrawing;
import com.github.anthonyo.swingifts.service.GiftDrawingService;
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
 * REST controller for managing {@link com.github.anthonyo.swingifts.domain.GiftDrawing}.
 */
@RestController
@RequestMapping("/api")
public class GiftDrawingResource {

    private final Logger log = LoggerFactory.getLogger(GiftDrawingResource.class);

    private static final String ENTITY_NAME = "giftDrawing";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GiftDrawingService giftDrawingService;

    public GiftDrawingResource(GiftDrawingService giftDrawingService) {
        this.giftDrawingService = giftDrawingService;
    }

    /**
     * {@code POST  /gift-drawings} : Create a new giftDrawing.
     *
     * @param giftDrawing the giftDrawing to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new giftDrawing, or with status {@code 400 (Bad Request)} if the giftDrawing has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/gift-drawings")
    public ResponseEntity<GiftDrawing> createGiftDrawing(@Valid @RequestBody GiftDrawing giftDrawing) throws URISyntaxException {
        log.debug("REST request to save GiftDrawing : {}", giftDrawing);
        if (giftDrawing.getId() != null) {
            throw new BadRequestAlertException("A new giftDrawing cannot already have an ID", ENTITY_NAME, "idexists");
        }
        GiftDrawing result = giftDrawingService.save(giftDrawing);
        return ResponseEntity.created(new URI("/api/gift-drawings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /gift-drawings} : Updates an existing giftDrawing.
     *
     * @param giftDrawing the giftDrawing to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated giftDrawing,
     * or with status {@code 400 (Bad Request)} if the giftDrawing is not valid,
     * or with status {@code 500 (Internal Server Error)} if the giftDrawing couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/gift-drawings")
    public ResponseEntity<GiftDrawing> updateGiftDrawing(@Valid @RequestBody GiftDrawing giftDrawing) throws URISyntaxException {
        log.debug("REST request to update GiftDrawing : {}", giftDrawing);
        if (giftDrawing.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        GiftDrawing result = giftDrawingService.save(giftDrawing);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, giftDrawing.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /gift-drawings} : get all the giftDrawings.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of giftDrawings in body.
     */
    @GetMapping("/gift-drawings")
    public List<GiftDrawing> getAllGiftDrawings() {
        log.debug("REST request to get all GiftDrawings");
        return giftDrawingService.findAll();
    }

    /**
     * {@code GET  /gift-drawings/:id} : get the "id" giftDrawing.
     *
     * @param id the id of the giftDrawing to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the giftDrawing, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/gift-drawings/{id}")
    public ResponseEntity<GiftDrawing> getGiftDrawing(@PathVariable Long id) {
        log.debug("REST request to get GiftDrawing : {}", id);
        Optional<GiftDrawing> giftDrawing = giftDrawingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(giftDrawing);
    }

    /**
     * {@code DELETE  /gift-drawings/:id} : delete the "id" giftDrawing.
     *
     * @param id the id of the giftDrawing to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/gift-drawings/{id}")
    public ResponseEntity<Void> deleteGiftDrawing(@PathVariable Long id) {
        log.debug("REST request to delete GiftDrawing : {}", id);
        giftDrawingService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
