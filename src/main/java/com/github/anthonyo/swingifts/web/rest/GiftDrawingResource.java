package com.github.anthonyo.swingifts.web.rest;

import com.github.anthonyo.swingifts.service.GiftDrawingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
