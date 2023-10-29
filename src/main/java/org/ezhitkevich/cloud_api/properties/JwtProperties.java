package org.ezhitkevich.cloud_api.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.time.Duration;

@Getter
@ConfigurationProperties(prefix = "jwt")
@ConfigurationPropertiesScan("classpath:jwt/jwt.properties")
public class JwtProperties {

    private String secret;

    private Duration lifetime;

    private String tokenType;

    private String header;
    @ConstructorBinding
    public JwtProperties(String secret, Duration lifetime, String tokenType, String header) {
     this.secret = secret;
     this.lifetime = lifetime;
     this.tokenType = tokenType;
     this.header = header;
    }
}

