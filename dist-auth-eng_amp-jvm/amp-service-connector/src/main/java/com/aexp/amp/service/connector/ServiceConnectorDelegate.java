package com.aexp.amp.service.connector;

import com.aexp.amp.common.context.AMPContext;
import com.aexp.ea.observability.contract.TracingContext;
import com.aexp.ea.observability.model.OperationName;
import com.aexp.ea.observability.model.SpanReferenceType;
import com.aexp.grt.serviceconnector.schema.ServiceConnectorSchema;
import com.aexp.grt.serviceconnector.webclient.starter.ServiceConnectorBase;
import com.aexp.grt.serviceconnector.webclient.starter.WebClientServiceConfiguration;
import com.aexp.grt.serviceconnector.webclient.starter.factory.ServiceConfigurationFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.aexp.amp.common.context.BaggageItemKeys.REPLAY_TRANSACTION;
import static com.aexp.amp.common.util.Preconditions.requireContext;
import static com.aexp.grt.serviceconnector.webclient.starter.configuration.ServiceConnectorSharedConfiguration.WEBCLIENT_STARTER_QUALIFIER;
import static java.util.Objects.requireNonNull;
import static java.util.UUID.nameUUIDFromBytes;
import static org.apache.commons.lang3.BooleanUtils.toBoolean;
import static org.springframework.util.CollectionUntils.isEmpty;

public final class ServiceConnectorDelegate {

    public static final String SVC_CALL_SCEMA = "svcCallSchema";
    public static final String SVC_CALL_INPUT = "svcCallInput";
    public static final String SVC_CALL_RESPONSE = "svcCallResponse";
    private static final OperationName SERVICE_CONNECTOR = new OperationName("callServiceConnector");
    private final ServiceConfigurationFactory serviceConfigurationFactory;
    private final ServiceConnectorBase serviceConnector;
    private final ObjectMapper objectMapper;

    public ServiceConnectorDelegate(
            ServiceConfigurationFactory serviceConfigurationFactory,
            ServiceConnectorBase serviceConnector,
            @Qualifier(WEBCLIENT_STARTER_QUALIFIER) ObjectMapper objectMapper){
        this.serviceConfigurationFactory =
                requireNonNull(
                        serviceConfigurationFactory, "serviceConfigurationFactory is required and missing");
        this.serviceConnector =
                requireNonNull(
                        serviceConnector, "serviceConnector is required and missing"
                );
        this.objectMapper = requireNonNull(objectMapper, "objectMapper is required and missing");
    }

    public CompletableFuture<Map<String, Object>> exchangeCall(
            TracingConext callerCtx, String schemaJson, Map<String, Object> inputData){

        requireContext(callerCtx);
        requireNonNull(schemaJson, "schemaJson is required and missing");
        requireNonNull(inputData, "inputData is required and missing");

        TracingContext ctx = callerCtx.newChildContext(SERVICE_CONNECTOR, SpanReferenceType.CHILD_OF);

        ctx.info(
                SVC_CALL_SCEMA, Map.of(SVC_CALL_SCEMA, String.valueOf(generateHashCode(schemaJson))));
        ctx.info(SVC_CALL_INPUT, Map.of(SVC_CALL_INPUT, inputData));

        //For replay transactions return the response from Transaction
        if(toBoolean(ctx.getBaggageItem(REPLAY_TRANSACTION))){
            try{
                var cachedResponse = retrieveCachedResponse(ctx, schemaJson);

                return cachedResponse == null
                        ? CompletableFuture.completedFuture(null)
                        : CompletableFuture.completedFuture(
                                objectMapper.convertValue(cachedResponse, Map.class));

            }catch (Exception e){
                throw new RuntimeException("Replay data not available", e);
            }
        }

        //For LIVE transactions, call will be executed.
        Resource schema = new ClassPathResource(schemaJson);

        try(InputStream inputStream = schema.getInputStream()){
            requireNonNull(inputStream, "inputStream is required and missing.");

            ServiceConnectorSchema serviceConnectorSchema =
                    objectMapper.readValue(inputStream, ServiceConnectorSchema.class);

            WebClientServiceConfiguration<Map<String, Object>, String, String, Map<String, Object>, Map>
                    serviceConfiguration = serviceConfigurationFactory.create(serviceConnectorSchema);

            var response = serviceConnector.exchange(serviceConfiguration, inputData);

            ctx.info(SVC_CALL_RESPONSE, Map.of(SVC_CALL_RESPONSE, response));

            return CompletableFuture.completedFuture(objectMapper.convertValue(response, Map.class));

        }catch (Exception e){
            throw new RuntimeException("Service call failed", e);
        }finally {
            ctx.finishActiveSpan();
        }
    }

    /*
    * @return hashcode - generated using the inputs being passed
    * <p> Note: This hashing function uses commons serializer instead of Jackson becuase the
    * latter doesn't guarantee order and hence the hashing becomes inconsistent
    * */
    public static long generateHashCode(Object... inputs){
        requireNonNull(inputs, "input is required and missing");

        return nameUUIDFromBytes(SerializationUtils.serialize(inputs)).getMostSignificantBits();
    }

    /*
    * @param ctx of the transaction
    * @param inputs used to retrieve the cached response
    * @return cached response of the replay transaction
    * */
    private Object retrieveCachedResponse(TracingContext ctx, Object... inputs){
        var replayMessage = ((AMPContext) ctx).getReplayMessage();

        if(isEmpty(replayMessage)){
            ctx.warn("Replay map is empty");
            return null;
        }

        var hashCode = generateHashCode(inputs);
        var cachedTransactionInfo = replayMessage.get(String.valueOf(hashCode));

        if(cachedTransactionInfo == null){
            // Scenario where Service Connector calls timed-out and this value would be null in processor

            //layer

            ctx.warn("Service Call cached response is null in replay map", Map.of("hashCode", hashCode));
            return null;
        }
        return ((Map<String, Object>) cachedTransactionInfo).get(SVC_CALL_RESPONSE);
    }
}
