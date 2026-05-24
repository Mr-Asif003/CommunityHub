package com.communityhub.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    /**
     * ✅ FIX: Use Jackson2ObjectMapperBuilderCustomizer instead of declaring
     * a new @Bean ObjectMapper.
     *
     * The old approach created a SECOND ObjectMapper that Spring MVC never
     * actually used — Spring Boot's auto-configured one (which handles HTTP
     * response serialization) was a separate instance without JavaTimeModule,
     * so LocalDateTime fields threw a 500 "no serializer found" error.
     *
     * This customizer plugs into Spring Boot's builder and patches the ONE
     * ObjectMapper that MVC, WebSocket message converters, and MongoDB all share.
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder
                // Register JavaTimeModule so LocalDateTime serializes as
                // "2025-01-15T14:30:00" instead of throwing an exception
                .modules(new JavaTimeModule())

                // Write dates as ISO-8601 strings, not numeric timestamps
                // e.g. "2025-01-15T14:30:00" not 1736951400.000000000
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}