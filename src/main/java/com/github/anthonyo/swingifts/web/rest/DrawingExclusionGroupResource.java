package com.github.anthonyo.swingifts.web.rest;

import com.github.anthonyo.swingifts.service.DrawingExclusionGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
