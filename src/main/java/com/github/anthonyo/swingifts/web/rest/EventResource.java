package com.github.anthonyo.swingifts.web.rest;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.anthonyo.swingifts.domain.Event;
import com.github.anthonyo.swingifts.security.SecurityUtils;
import com.github.anthonyo.swingifts.service.EventService;
import com.github.anthonyo.swingifts.service.errors.EntityNotFoundException;
import com.github.anthonyo.swingifts.web.rest.errors.BadRequestAlertException;

import com.github.anthonyo.swingifts.web.rest.vm.JsonViews;
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
 * REST controller for managing {@link com.github.anthonyo.swingifts.domain.Event}.
 */
@RestController
@RequestMapping("/api")
public class EventResource {

    private final Logger log = LoggerFactory.getLogger(EventResource.class);

    private static final String ENTITY_NAME = "event";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EventService eventService;

    public EventResource(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * {@code POST  /events} : Create a new event.
     *
     * @param event the event to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new event, or with status {@code 400 (Bad Request)} if the event has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/events")
    public ResponseEntity<Event> createEvent(@Valid @RequestBody Event event) throws URISyntaxException, EntityNotFoundException {
        log.debug("REST request to save Event : {}", event);
        if (event.getId() != null) {
            throw new BadRequestAlertException("A new event cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Event result = eventService.save(event, SecurityUtils.getCurrentUserLoginOrThrowBadCredentials());
        return ResponseEntity.created(new URI("/api/events/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getName()))
            .body(result);
    }

    /**
     * {@code PUT  /events} : Updates an existing event.
     *
     * @param event the event to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated event,
     * or with status {@code 400 (Bad Request)} if the event is not valid,
     * or with status {@code 500 (Internal Server Error)} if the event couldn't be updated.
     */
    @PutMapping("/events")
    public ResponseEntity<Event> updateEvent(@Valid @RequestBody Event event) throws EntityNotFoundException {
        log.debug("REST request to update Event : {}", event);
        if (event.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Event result = eventService.save(event, SecurityUtils.getCurrentUserLoginOrThrowBadCredentials());
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getName()))
            .body(result);
    }

    /**
     * {@code GET  /events} : get all the events.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of events in body.
     */
    @GetMapping("/events")
    @JsonView(JsonViews.EventGet.class)
    public List<Event> getEventsForCurrentUser() {
        log.debug("REST request to get all Events");
        return eventService.findForRequesterUserLogin(SecurityUtils.getCurrentUserLoginOrThrowBadCredentials());
    }

    /**
     * {@code GET  /events/:id} : get the "id" event.
     *
     * @param id the id of the event to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the event, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/events/{id}")
    @JsonView(JsonViews.EventGet.class)
    public ResponseEntity<Event> getEvent(@PathVariable Long id) {
        log.debug("REST request to get Event : {}", id);
        Optional<Event> event = eventService.findOneForRequesterUserLogin(id, SecurityUtils.getCurrentUserLoginOrThrowBadCredentials());
        return ResponseUtil.wrapOrNotFound(event);
    }

    /**
     * {@code DELETE  /events/:id} : delete the "id" event.
     *
     * @param id the id of the event to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) throws EntityNotFoundException {
        log.debug("REST request to delete Event : {}", id);
        final Event result = eventService.delete(id, SecurityUtils.getCurrentUserLoginOrThrowBadCredentials());
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, result.getName())).build();
    }

    @PostMapping("/events/{id}/draw-gifts") // POST as this is not an idempotent action: it changes data, see https://softwareengineering.stackexchange.com/a/261675/119279
    public void drawGifts(@PathVariable Long id) throws EntityNotFoundException {
        log.debug("Request to launch the gift drawing of the Event : {}", id);
        eventService.drawGifts(id, SecurityUtils.getCurrentUserLoginOrThrowBadCredentials());
    }

    /**
     * {@code GET  /public/events/:publicKey} : get the "publicKey" event.
     *
     * @param publicKey the publicKey of the event to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the event, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/public/events/{publicKey}")
    @JsonView(JsonViews.EventPublicGet.class)
    public ResponseEntity<Event> getEventByPublicKey(@PathVariable String publicKey) {
        log.debug("REST request to get Event by public key: {}", publicKey);
        Optional<Event> event = eventService.findOneByPublicKeyAndPublicKeyEnabledIsTrue(publicKey);
        return ResponseUtil.wrapOrNotFound(event);
    }
}
