package com.aexp.amp.service.connector.config;


import com.aexp.amp.service.connector.ServiceConnectorDelegate;
import com.aexp.grt.serviceconnector.webclient.starter.ServiceConnectorBase;
import com.aexp.grt.serviceconnector.webclient.starter.confirguration.*;
import com.aexp.grt.serviceconnector.webclient.starter.factory.RequestSchemaUpdateFunctionFactory;
import com.aexp.grt.serviceconnector.webclient.starter.factory.ServiceConfigurationFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotstion.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import({
   WebClientStarterConfiguration.class,
   WebClientConfiguration.class,
   DefaultErrorConsumerConfiguration.class,
   DynamicExpressionFunctionConfiguration.class,
   RequestSchemaUpdateFunctionConfiguration.class,
   RequestSchemaUpdateFunctionFactory.class,
   ErrorReponseGenerationConfiguration.class,
   SoapStarterConfiguration.class

})

public class ServiceConnectorDelegateConfiguration {

    @Bean
    public ServiceConnectorDelegate serviceConnectorWrapper(
            ServiceConfigurationFactory serviceConfigurationFactory,
            ServiceConnectorBase serviceConnector,
            @Qualifier(WEBCLIENT_STARTER_QUALIFIER) ObjectMapper objectMapper){
        requireNonNull(
                serviceConfigurationFactory, "serviceConfigurationFactory is required and missing");
        requireNonNull(
                serviceConnector, "serviceConnector is required and missing");
        requireNonNull(
                objectMapper, "objectMapper is required and missing");

        return new ServiceConnectorDelegate(
                serviceConfigurationFactory, serviceConnector, objectMapper);
    }




}
