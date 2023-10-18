package org.ezhitkevich.authorization_service.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Getter
@ConfigurationProperties(prefix = "jwt")
@ConfigurationPropertiesScan("classpath:jwt/jwt.properties")
public class JwtProperties {

    private String secret;

    private Duration lifetime;

    private String tokenType;

    @ConstructorBinding
    public JwtProperties(String secret, Duration lifetime, String tokenType) {
     this.secret = secret;
     this.lifetime = lifetime;
     this.tokenType = tokenType;
    }
}

