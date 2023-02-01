package com.aexp.metadata.demo.service;


import static com.aexp.amp.common.util.Preconditions.requireContext;
import static com.aexp.ea.lifecycle.model.DesiredAction.ACTIVATE;
import static com.aexp.ea.lifecycle.model.DesiredAction.STOP;
import static com.aexp.ea.lifecycle.model.DesiredAction.RUNNING;
import static com.aexp.ea.lifecycle.model.DesiredAction.STOPPED;
import static com.aexp.ea.lifecycle.model.DesiredAction.UNKNOWN;
import static java.util.Objects.requireNonNull;

import com.aexp.amp.common.context.AMPContext;
import com.aexp.amp.metadata.commons.contract.MetadataHelper;
import com.aexp.ea.lifecycle.impl.DefaultLifeCycleAware;
import com.aexp.ea.lifecycle.legacy.StateChangeDecisionLegacy;
import com.aexp.ea.lifecycle.model.StateChangeDecision;
import com.aexp.ea.lifecycle.model.StateTransition;
import com.aexp.ea.observability.model.OperationName;
import com.google.common.collect.ImmutableMap;
import io.opentracing.Tracer;
import java.time.Duration;
import org.apache.logging.log4j.logMapper;
import org.apache.logging.log4j.logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
org.springframework.context.event.ContextClosedEvent;


public class MetadataDemoLifeCycleService {
    private static final Logger LOGGER = logManager.getLogger(MetadataDemoLifeCycleService.class);
    private static final Duration DEFAULT_STATE_CHANGE_TIMEOUT = Duration.ofSeconds(120L);
    private static final OperationName OPERATION = new OperationName("initializeMetadataDemo");
    private final ConfigurationApplicationContext applicationContext;
    private final DefaultLifeCycleAware defaultLifeCycleAware;
    private final MetadataHelper<String, Object> metadataHelper;
    private final Tracer tracer;
    private final String applicationName;

    public MetadataDemoLifeCycleService(
            ConfigurationApplicationContext applicationContext,
            MetadataHelper<String, Object> metadataHelper,
            Tracer tracer){
        this.metadataHelper = requireNonNull(metadataHelper, "metadataHelper is required and missing");
        this.tracer = requireNonNull(tracer, "tracer is required and missing");
        this.applicationContext = requireNonNull(applicationContext);
        defaultLifeCycleAware = constructLifeCycleStates(DEFAULT_STATE_CHANGE_TIMEOUT);

        applicationName =
                applicationContext.getEnvironment().getProperty("spring.application.name", "application");
        registerStarAndStop();
    }

    private void registerStarAndStop(){
        defaultLifeCycleAware.setDesiredStateLegacy(ACTIVATE);

        //register stop event
        applicationContext.addApplicationListener(
                (ApplicationListener<ContextClosedEvent>)
                        event -> defaultLifeCycleAware.setDesiredStateLegacy(STOP));
    }

    private DefaultLifeCycleAware constructLifeCycleStates(Duration stateChangeTimeout){
        return new DefaultLifeCycleAware(
                new ImmutableMap.Builder<StateTransition, StateChangeDecision>()
                .put(
                        new StateTransition(UNKNOWN, ACTIVATE),
                        new StateChangeDecisionLegacy(this::onActivate, RUNNING, stateChangeTimeout)
                        .getAsModern())
                .put(
                        new StateTransition(UNKNOWN, STOP),
                        new StateChangeDecisionLegacy(this::onStop, STOPPED, stateChangeTimeout)
                                .getAsModern())
                        .build());
    }

    private void onActivate(){
        LOGGER.info("Starting {}", applicationName);
        metadataHelper.inti(AMPContext.newContext(OPERATION, tracer));
        LOGGER.info("{} has started", applicationName);
    }

    private void onStop(){
        LOGGER.info("Stoping {}", applicationName);
        applicationContext.close();
        LOGGER.info("{} has stopped", applicationName);
    }


}
