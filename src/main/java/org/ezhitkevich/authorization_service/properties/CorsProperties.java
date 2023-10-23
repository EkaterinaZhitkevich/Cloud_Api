package org.ezhitkevich.authorization_service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "cors")
@Component
public class CorsProperties {

    private String mapping;

    private String[] methods;

    private String[] headers;

    private String[] origins;

}
