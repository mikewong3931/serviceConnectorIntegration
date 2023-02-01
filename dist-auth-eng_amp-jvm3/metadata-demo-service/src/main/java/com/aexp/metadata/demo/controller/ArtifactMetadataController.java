package com.aexp.metadata.demo.controller;

import static com.aexp.amp.common.util.Preconditions.requirePopulatedMap;
import static com.aexp.amp.metadata.commons.util.Qualidiers.METADATA_JOURNEY_ARTIFACTS;
import static java.util.Objects.requireNonNull;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestContoller
@RequestMapping("/metadata/artifact")
public class ArtifactMetadataController {
    private final MetadataCacheService artifactMetadataCacheService;

    public ArtifactMetadataController(
            @Qualifier(METADATA_JOURNEY_ARTIFACTS) MetadataCacheService artifactMetadataCacheService) {
        this.artifactMetadataCacheService =
                requireNonNull(
                        artifactMetadataCacheService, "artifactMetadataCacheService is required and missing.");
    }

    @GetMapping
    public JourneyArtifacts getArtifactMetadata(
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

        return (JourneyArtifacts) artifactMetadataCacheService.get(requestEntity);


    }
}
