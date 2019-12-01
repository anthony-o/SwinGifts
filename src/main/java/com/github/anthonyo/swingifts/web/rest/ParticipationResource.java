package com.github.anthonyo.swingifts.web.rest;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.anthonyo.swingifts.domain.Participation;
import com.github.anthonyo.swingifts.security.SecurityUtils;
import com.github.anthonyo.swingifts.service.ParticipationService;
import com.github.anthonyo.swingifts.service.errors.EntityNotFoundException;
import com.github.anthonyo.swingifts.web.rest.errors.BadRequestAlertException;

import com.github.anthonyo.swingifts.web.rest.vm.JsonViews;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import io.undertow.util.BadRequestException;
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
 * REST controller for managing {@link com.github.anthonyo.swingifts.domain.Participation}.
 */
@RestController
@RequestMapping("/api")
public class ParticipationResource {

    private final Logger log = LoggerFactory.getLogger(ParticipationResource.class);

    private static final String ENTITY_NAME = "participation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ParticipationService participationService;

    public ParticipationResource(ParticipationService participationService) {
        this.participationService = participationService;
    }

    /**
     * {@code POST  /participations} : Create a new participation.
     *
     * @param participation the participation to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new participation, or with status {@code 400 (Bad Request)} if the participation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/participations")
    public ResponseEntity<Participation> createParticipation(@Valid @RequestBody Participation participation) throws URISyntaxException, EntityNotFoundException {
        log.debug("REST request to save Participation : {}", participation);
        if (participation.getId() != null) {
            throw new BadRequestAlertException("A new participation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Participation result = participationService.save(participation, SecurityUtils.getCurrentUserLoginOrThrowBadCredentials());
        return ResponseEntity.created(new URI("/api/participations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getUserAlias()))
            .body(result);
    }

    /**
     * {@code PUT  /participations} : Updates an existing participation.
     *
     * @param participation the participation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated participation,
     * or with status {@code 400 (Bad Request)} if the participation is not valid,
     * or with status {@code 500 (Internal Server Error)} if the participation couldn't be updated.
     */
    @PutMapping("/participations")
    public ResponseEntity<Participation> updateParticipation(@Valid @RequestBody Participation participation) throws EntityNotFoundException {
        log.debug("REST request to update Participation : {}", participation);
        if (participation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Participation result = participationService.save(participation, SecurityUtils.getCurrentUserLoginOrThrowBadCredentials());
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, participation.getUserAlias()))
            .body(result);
    }

    /**
     * {@code GET  /participations/by-event-id/:eventId} : get participations for the "eventId".
     *
     * @param eventId the id of the event to retrieve participations for.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of participations in body.
     */
    @GetMapping("/participations/by-event-id/{eventId}")
    @JsonView(JsonViews.ParticipationGet.class)
    public List<Participation> getParticipationsByEventId(@PathVariable Long eventId) {
        log.debug("REST request to get all Participations");
        return participationService.findByEventId(eventId, SecurityUtils.getCurrentUserLoginOrThrowBadCredentials());
    }

    /**
     * {@code GET  /participations/:id} : get the "id" participation.
     *
     * @param id the id of the participation to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the participation, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/participations/{id}")
    @JsonView(JsonViews.ParticipationGet.class)
    public ResponseEntity<Participation> getParticipation(@PathVariable Long id) {
        log.debug("REST request to get Participation : {}", id);
        Optional<Participation> participation = participationService.findOne(id, SecurityUtils.getCurrentUserLoginOrThrowBadCredentials());
        return ResponseUtil.wrapOrNotFound(participation);
    }

    /**
     * {@code DELETE  /participations/:id} : delete the "id" participation.
     *
     * @param id the id of the participation to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/participations/{id}")
    public ResponseEntity<Void> deleteParticipation(@PathVariable Long id) throws EntityNotFoundException {
        log.debug("REST request to delete Participation : {}", id);
        final Participation participation = participationService.delete(id, SecurityUtils.getCurrentUserLoginOrThrowBadCredentials());
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, participation.getUserAlias())).build();
    }

    @PostMapping("/public/participations")
    @JsonView(JsonViews.ParticipationGet.class)
    public ResponseEntity<Participation> createPublicParticipation(@RequestBody Participation participation) throws URISyntaxException, EntityNotFoundException {
        log.debug("REST request to save public Participation : {}", participation);
        if (participation.getId() != null) {
            throw new BadRequestAlertException("A new participation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Participation result = participationService.savePublic(participation, SecurityUtils.getCurrentUserLoginOrThrowBadCredentials());
        return ResponseEntity.created(new URI("/api/participations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PostMapping("/participations/{id}/set-user-with-current-user")
    @JsonView(JsonViews.ParticipationGet.class)
    public ResponseEntity<Participation> updateUserWithCurrentUser(@PathVariable Long id, @RequestBody String eventPublicKey) throws EntityNotFoundException, BadRequestException {
        log.debug("REST request to set-user-with-current-user : {}, {}", id, eventPublicKey);
        Participation result = participationService.updateUserWithCurrentUser(id, eventPublicKey, SecurityUtils.getCurrentUserLoginOrThrowBadCredentials());
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }
}
