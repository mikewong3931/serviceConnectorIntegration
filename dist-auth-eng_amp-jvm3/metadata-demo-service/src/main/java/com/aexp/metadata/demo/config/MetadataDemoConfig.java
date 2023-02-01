package com.aexp.metadata.demo.config;

import static java.util.Objects.requireNonNull;

import com.aexp.amp.common.config.TracingBeans;
import com.aexp.amp.common.context.AMPContext;
import com.aexp.amp.metada.commons.contract.MetadataHelper;
import com.aexp.amp.metada.executor.config.MetadataHelperConfigBeans;
import com.aexp.amp.metada.sdk.config.ArtifactMetadataBeans;
import com.aexp.amp.metada.sdk.config.EntityVariablesMetadataBeans;
import com.aexp.amp.metada.sdk.config.ProcessMetadataBeans;
import com.aexp.amp.metada.sdk.config.QueryMetadataBeans;
import com.aexp.amp.metada.sdk.config.ServiceMetadataBeans;
import com.aexp.ea.observability.model.OperationName;
import com.aexp.metadata.demo.controller.ArtifactMetadataController;
import com.aexp.metadata.demo.controller.EntityVariablesMetadataController;
import com.aexp.metadata.demo.controller.MetadataHelperController;
import com.aexp.metadata.demo.controller.ProcessMetadataController;
import com.aexp.metadata.demo.controller.QuerytMetadataController;
import com.aexp.metadata.demo.controller.ServiceMetadataController;
import io.opentracing.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        ArtifactMetadataBeans.class,
        ArtifactMetadataController.class,
        EntityVariablesMetadataBeans.class,
        EntityVariablesMetadataController.class,
        MetadataHelperConfigBeans.class,
        MetadataHelperController.class,
        ProcessMetadataBeans.class,
        ProcessMetadataController.class,
        QueryMetadataBeans.class,
        QueryMetadataController.class,
        ServiceMetadataBeans.class,
        ServiceMetadataController.class,
        TracingBeans.class,
        WiremockBeans.class
})

public class MetadataDemoConfig {

    @Bean
    public boolean initMetadataHelper(Tracer tracer, MetadataHelper<String, Object> metadataHelper)(

        requireNonNull(
                tracer, "tracer is required and missing");
        requireNonNull(
                metadataHelper, "metadataHelper is required and missing");
        metadataHelper.inti(AMPContext.newContext(new OperationName("intializeMetadataDemo"), tracer));

        return true;
    }

}
