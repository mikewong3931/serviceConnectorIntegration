package com.aexp.metadata.demo.controller;

import static com.aexp.amp.common.util.Preconditions.requirePopulatedMap;
import static com.aexp.amp.metadata.commons.util.Qualidiers.METADATA_JOURNEY_PROCESS;
import static java.util.Objects.requireNonNull;

import com.aexp.amp.metadata.commons.model.JourneyProcess;
import com.aexp.amp.metadata.service.MetadataCacheService;
import com.aexp.amp.proto.model.Entitiy;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

public class ProcessMetadataController {
    private final MetadataCacheService processMetadataCacheService;

    public ProcessMetadataController(
            @Qualifier(METADATA_JOURNEY_PROCESS) MetadataCacheService processMetadataCacheService) {
        this.processMetadataCacheService =
                requireNonNull(
                        processMetadataCacheService, "processMetadataCacheService is required and missing.");
    }

    @GetMapping
    public JourneyProcess getProcessMetadata(
            @RequestHeader Map<String, String> headers, @RequestBoday Map<String, Object> playload) {
        requirePopulateMap(headers, "headers connot be null or empty");

        var containerName = headers.get("container_name");
        var platformName = headers.get("platform_name");

        var requestEntity =
                Entity.newBuilder()
                        .setContainerName(containerName)
                        .setPlatformName(platformName)
                        .setVersion(1)
                        .build();

        return (JourneyProcess) processMetadataCacheService.get(requestEntity);
    }
}
