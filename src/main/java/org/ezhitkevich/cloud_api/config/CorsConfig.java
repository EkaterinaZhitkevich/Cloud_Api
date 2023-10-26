package org.ezhitkevich.cloud_api.config;

import lombok.RequiredArgsConstructor;
import org.ezhitkevich.cloud_api.properties.CorsProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
