package org.ezhitkevich.authorization_service.config;

import lombok.RequiredArgsConstructor;
import org.ezhitkevich.authorization_service.properties.CorsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CorsConfig implements WebMvcConfigurer {

    private final CorsProperties corsProperties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping(corsProperties.getMapping())
                .allowCredentials(true)
                .allowedOrigins(corsProperties.getOrigins())
                .allowedMethods(corsProperties.getMethods())
                .allowedHeaders(corsProperties.getHeaders());
    }
}
