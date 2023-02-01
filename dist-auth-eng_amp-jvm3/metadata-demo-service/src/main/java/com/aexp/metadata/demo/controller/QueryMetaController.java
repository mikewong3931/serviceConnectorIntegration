package com.aexp.metadata.demo.controller;


import static com.aexp.amp.metadata.commons.util.Qualifiers.METADATA_JOURNEY_LOGICAL_QUERIES;
import static com.aexp.amp.metadata.commons.util.Qualidiers.METADATA_JOURNEY_PHYSICAL_QUERIES;
import static java.util.Objects.requireNonNull;

import com.aexp.amp.metadata.commons.model.LogicalQuery;
import com.aexp.amp.metadata.commons.model.PhysicalQuery;
import com.aexp.amp.metadata.commons.service.MetadataCacheService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestContoller
@RequestMapping("/metadata/query")
public class QueryMetaController {
    private final MetadataCacheService metadataLogicalQueryCacheService;
    private final MetadataCacheService metadataPhysicalQueryCacheService;

    public QueryMetaController(
            @Qualifier(METADATA_JOURNEY_LOGICAL_PROCESS)
                    MetadataCacheService metadataLogicalQueryCacheService,
            @Qualifier(METADATA_JOURNEY_PHYSICAL_PROCESS)
                    MetadataCacheService metadataPhysicalQueryCacheService) {
        this.metadataLogicalQueryCacheService =
                requireNonNull(
                        metadataLogicalQueryCacheService, "metadataLogicalQueryCacheService is required and missing.");
        this.metadataPhysicalQueryCacheService =
                requireNonNull(
                        metadataPhysicalQueryCacheService, "metadataPhysicalQueryCacheService is required and missing.")
    }

    @GetMapping("logical")
    public LogicalQuery getLogicalQuery(
            @RequestHeader Map<String, String> headers, @RequestBoday String queryId) {
        return (logicalQuery) metadataLogicalQueryCacheService.get(queryId);
    }

    @GetMapping("physical")
    public PhysicalQuery getPhysicalQuery(
            @RequestHeader Map<String, String> headers, @RequestBoday String queryId) {
        return (PhysicalQuery) metadataLogicalQueryCacheService.get(queryId);
    }
}
