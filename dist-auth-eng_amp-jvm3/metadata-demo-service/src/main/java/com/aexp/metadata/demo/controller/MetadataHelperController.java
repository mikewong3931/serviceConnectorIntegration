package com.aexp.metadata.demo.controller;

import static com.aexp.amp.common.util.Preconditions.requirePopulatedMap; //

import static com.aexp.metadata.demo.util.Constants.PAYMENT_EVENT_PAYLOAD_KEY;
import static com.aexp.metadata.demo.util.Constants.PAYMENT_EVENT_PAYLOAD_TYPE_REF;
import static com.aexp.metadata.demo.util.Constants.PROTECTED_DATA_KEY;
import static com.aexp.metadata.demo.util.Constants.PROTECTED_DATA_TYPE_REF;
import static com.aexp.metadata.demo.util.Constants.UNPROTECTED_DATA_KEY;
import static com.aexp.metadata.demo.util.Constants.UNPROTECTED_DATA_TYPE_REF;
import static com.aexp.metadata.demo.util.TracingUtils.createContext;
import static java.util.Objects.requireNonNull; //
import com.aexp.amp.metadata.commons.contract.MetadataHelper;
import com.aexp.amp.proto.model.Entity;//
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentracing.Tracer;
import java.util.Map;//
import org.springframework.web.bind.annotation.GetMapping; //
import org.springframework.web.bind.annotation.RequestBody; //
import org.springframework.web.bind.annotation.RequestHeader; //
import org.springframework.web.bind.annotation.RequestMapping; //
import org.springframework.web.bind.annotation.RestController; //


import java.util.Map;

@RestContoller
@RequestMapping("/metadata/helper")
public class MetadataHelperController {
    private final MetadataHelper metadataHelper;
    private final ObjectMapper objectMapper;
    private final Tracer tracer;

    public MetadataHelperController(
            MetadataHelper metadataHelper, ObjectMapper objectMapper, Tracer tracer
    ){
        this.metadataHelper = requireNonNull(metadataHelper, "metadataHelper is required and missing.");
        this.objectMapper = requireNonNull(objectMapper, "objectMapper is required and missing.");
        this.tracer = requireNonNull(tracer, "tracer is required and missing.");
    }

    @GetMapping
    public EntityVariables runMetadataHelper(
            @RequestHeader Map<String,String> headers, @RequestBoday Map<String, Object> playload){
        requirePopulateMap(headers, "headers connot be null or empty");
        requirePopulateMap(playload, "playload connot be null or empty");

        var processName = headers.get("process_name");
        var containerName = headers.get("container_name");
        var platformName = headers.get("platform_name");

        var requestEntity =
                Entity.newBuilder()
                        .setContainerName(containerName)
                        .setPlatformName(platformName)
                        .setVersion(1)
                        .build();
        if(playload.containsKey(UNPROTECTED_DATA_KEY)){
            playload.put(
                    UNPROTECTED_DATA_KEY,
                    objectMapper.convertValue(playload.get(UNPROTECTED_DATA_KEY), UNPROTECTED_DATA_TYPE_REF));
        }

        if(playload.containsKey(PAYMENT_EVENT_PAYLOAD_KEY)){
            playload.put(
                    PAYMENT_EVENT_PAYLOAD_KEY,
                    objectMapper.convertValue(playload.get(PAYMENT_EVENT_PAYLOAD_KEY), PAYMENT_EVENT_PAYLOAD_TYPE_REF));
        }

        var response =
                metadataHelper.apply(createContext(tracer, headers), playload, requestEntity, platformName);
        return response.toString();

    }
}
