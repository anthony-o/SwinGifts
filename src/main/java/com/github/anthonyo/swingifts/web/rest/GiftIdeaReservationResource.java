package com.github.anthonyo.swingifts.web.rest;

import com.github.anthonyo.swingifts.domain.GiftIdeaReservation;
import com.github.anthonyo.swingifts.service.GiftIdeaReservationService;
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
 * REST controller for managing {@link com.github.anthonyo.swingifts.domain.GiftIdeaReservation}.
 */
@RestController
@RequestMapping("/api")
public class GiftIdeaReservationResource {

    private final Logger log = LoggerFactory.getLogger(GiftIdeaReservationResource.class);

    private static final String ENTITY_NAME = "giftIdeaReservation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GiftIdeaReservationService giftIdeaReservationService;

    public GiftIdeaReservationResource(GiftIdeaReservationService giftIdeaReservationService) {
        this.giftIdeaReservationService = giftIdeaReservationService;
    }

    /**
     * {@code POST  /gift-idea-reservations} : Create a new giftIdeaReservation.
     *
     * @param giftIdeaReservation the giftIdeaReservation to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new giftIdeaReservation, or with status {@code 400 (Bad Request)} if the giftIdeaReservation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/gift-idea-reservations")
    public ResponseEntity<GiftIdeaReservation> createGiftIdeaReservation(@Valid @RequestBody GiftIdeaReservation giftIdeaReservation) throws URISyntaxException {
        log.debug("REST request to save GiftIdeaReservation : {}", giftIdeaReservation);
        if (giftIdeaReservation.getId() != null) {
            throw new BadRequestAlertException("A new giftIdeaReservation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        GiftIdeaReservation result = giftIdeaReservationService.save(giftIdeaReservation);
        return ResponseEntity.created(new URI("/api/gift-idea-reservations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /gift-idea-reservations} : Updates an existing giftIdeaReservation.
     *
     * @param giftIdeaReservation the giftIdeaReservation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated giftIdeaReservation,
     * or with status {@code 400 (Bad Request)} if the giftIdeaReservation is not valid,
     * or with status {@code 500 (Internal Server Error)} if the giftIdeaReservation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/gift-idea-reservations")
    public ResponseEntity<GiftIdeaReservation> updateGiftIdeaReservation(@Valid @RequestBody GiftIdeaReservation giftIdeaReservation) throws URISyntaxException {
        log.debug("REST request to update GiftIdeaReservation : {}", giftIdeaReservation);
        if (giftIdeaReservation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        GiftIdeaReservation result = giftIdeaReservationService.save(giftIdeaReservation);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, giftIdeaReservation.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /gift-idea-reservations} : get all the giftIdeaReservations.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of giftIdeaReservations in body.
     */
    @GetMapping("/gift-idea-reservations")
    public List<GiftIdeaReservation> getAllGiftIdeaReservations() {
        log.debug("REST request to get all GiftIdeaReservations");
        return giftIdeaReservationService.findAll();
    }

    /**
     * {@code GET  /gift-idea-reservations/:id} : get the "id" giftIdeaReservation.
     *
     * @param id the id of the giftIdeaReservation to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the giftIdeaReservation, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/gift-idea-reservations/{id}")
    public ResponseEntity<GiftIdeaReservation> getGiftIdeaReservation(@PathVariable Long id) {
        log.debug("REST request to get GiftIdeaReservation : {}", id);
        Optional<GiftIdeaReservation> giftIdeaReservation = giftIdeaReservationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(giftIdeaReservation);
    }

    /**
     * {@code DELETE  /gift-idea-reservations/:id} : delete the "id" giftIdeaReservation.
     *
     * @param id the id of the giftIdeaReservation to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/gift-idea-reservations/{id}")
    public ResponseEntity<Void> deleteGiftIdeaReservation(@PathVariable Long id) {
        log.debug("REST request to delete GiftIdeaReservation : {}", id);
        giftIdeaReservationService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
