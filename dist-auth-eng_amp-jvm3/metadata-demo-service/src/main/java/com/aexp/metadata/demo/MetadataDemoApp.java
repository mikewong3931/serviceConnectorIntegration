package com.aexp.metadata.demo;

import com.aexp.metadata.demo.config.MetadataDemoConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.annotation.Configuration;
import org.springframework.boot.context.annotation.Import;

import javax.swing.*;

@Configuration
@Import(MetadataDemoConfig.class)
@EnableAutoConfiguration
///PMD.UseUtilityClass/
public class MetadataDemoApp {
    public static void main(String... args) {
        SpringApplication.run(MetadataDemoApp.class, args);
    }
}
