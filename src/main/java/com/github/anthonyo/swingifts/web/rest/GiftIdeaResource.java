package com.github.anthonyo.swingifts.web.rest;

import com.github.anthonyo.swingifts.domain.GiftIdea;
import com.github.anthonyo.swingifts.repository.GiftIdeaRepository;
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
 * REST controller for managing {@link com.github.anthonyo.swingifts.domain.GiftIdea}.
 */
@RestController
@RequestMapping("/api")
public class GiftIdeaResource {

    private final Logger log = LoggerFactory.getLogger(GiftIdeaResource.class);

    private static final String ENTITY_NAME = "giftIdea";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GiftIdeaRepository giftIdeaRepository;

    public GiftIdeaResource(GiftIdeaRepository giftIdeaRepository) {
        this.giftIdeaRepository = giftIdeaRepository;
    }

    /**
     * {@code POST  /gift-ideas} : Create a new giftIdea.
     *
     * @param giftIdea the giftIdea to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new giftIdea, or with status {@code 400 (Bad Request)} if the giftIdea has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/gift-ideas")
    public ResponseEntity<GiftIdea> createGiftIdea(@Valid @RequestBody GiftIdea giftIdea) throws URISyntaxException {
        log.debug("REST request to save GiftIdea : {}", giftIdea);
        if (giftIdea.getId() != null) {
            throw new BadRequestAlertException("A new giftIdea cannot already have an ID", ENTITY_NAME, "idexists");
        }
        GiftIdea result = giftIdeaRepository.save(giftIdea);
        return ResponseEntity.created(new URI("/api/gift-ideas/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /gift-ideas} : Updates an existing giftIdea.
     *
     * @param giftIdea the giftIdea to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated giftIdea,
     * or with status {@code 400 (Bad Request)} if the giftIdea is not valid,
     * or with status {@code 500 (Internal Server Error)} if the giftIdea couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/gift-ideas")
    public ResponseEntity<GiftIdea> updateGiftIdea(@Valid @RequestBody GiftIdea giftIdea) throws URISyntaxException {
        log.debug("REST request to update GiftIdea : {}", giftIdea);
        if (giftIdea.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        GiftIdea result = giftIdeaRepository.save(giftIdea);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, giftIdea.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /gift-ideas} : get all the giftIdeas.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of giftIdeas in body.
     */
    @GetMapping("/gift-ideas")
    public List<GiftIdea> getAllGiftIdeas() {
        log.debug("REST request to get all GiftIdeas");
        return giftIdeaRepository.findAll();
    }

    /**
     * {@code GET  /gift-ideas/:id} : get the "id" giftIdea.
     *
     * @param id the id of the giftIdea to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the giftIdea, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/gift-ideas/{id}")
    public ResponseEntity<GiftIdea> getGiftIdea(@PathVariable Long id) {
        log.debug("REST request to get GiftIdea : {}", id);
        Optional<GiftIdea> giftIdea = giftIdeaRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(giftIdea);
    }

    /**
     * {@code DELETE  /gift-ideas/:id} : delete the "id" giftIdea.
     *
     * @param id the id of the giftIdea to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/gift-ideas/{id}")
    public ResponseEntity<Void> deleteGiftIdea(@PathVariable Long id) {
        log.debug("REST request to delete GiftIdea : {}", id);
        giftIdeaRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
