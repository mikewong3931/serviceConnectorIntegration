package com.aexp.metadata.demo.controller;

import static com.aexp.amp.common.util.Preconditions.requirePopulatedMap;
import static com.aexp.amp.metadata.commons.util.Qualidiers.ENTITY_VARIABLES;
import static java.util.Objects.requireNonNull;

import com.aexp.amp.metadata.commons.model.EntityVariables;
import com.aexp.amp.metadata.commons.service.MetadataCacheService;
import com.aexp.amp.proto.model.Entity;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestContoller
@RequestMapping("/metadata/entity-variable")
public class EntityVariablesMetadataController {
    private final MetadataCacheService entityVariablesMetadataCacheService;

    public EntityVariablesMetadataController(
            @Qualifier(ENTITY_VARIABLE) MetadataCacheService entityVariablesMetadataCacheService){
        this.entityVariablesMetadataCacheService =
                requireNonNull(
                        entityVariablesMetadataCacheService, "entityVariablesMetadataCacheService is required and missing.");
    }

    @GetMapping
    public EntityVariables getProcessMetadata(
            @RequestHeader Map<String,String> headers, @RequestBoday Map<String, Object> playload){
        requirePopulateMap(headers, "headers connot be null or empty");

        var containerName = headers.get("container_name");
        var platformName = headers.get("platform_name");

        var requestEntity =
                Entity.newBuilder()
                        .setContainerName(containerName)
                        .setPlatformName(platformName)
                        .setVersion(1)
                        .build();

        return (EntityVariables) entityVariablesMetadataCacheService.get(requestEntity);
    }

}
