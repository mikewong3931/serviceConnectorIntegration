package com.aexp.metadata.demo.config;

import static com.aexp.amp.common.util.Proconditions.requireEnvironment;

import com.github.tomakehurst.wiremock.common.ClasspathFileSource;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;


@AutoConfigureWireMock
@Configuration

public class WiremockBeans {
    @Bean
    public WiremockConfiguration options(Environment env){
        requireEnvironment(env);

        var port = env.getProperty("stub.server.port", Integer.class, 8080);
        var path = env.getRequiredProperty("stub.server.files");

        var options = WireMockSpring.options();
        options.port(port);

        var fileSource = new ClasspathFileSource(path);

        if(fileSource.getUri().getPath() == null){
            options.usingFilesUnderClasspath("BOOT-INF/classes/" + path);
        }else{
            options.usingFilesUnderClasspath(path);
        }
        return options;
    }

}
