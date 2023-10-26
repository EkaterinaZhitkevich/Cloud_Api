package org.ezhitkevich.cloud_api.service.security.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ezhitkevich.cloud_api.exception.InvalidJwtTokenException;
import org.ezhitkevich.cloud_api.jwt.JwtProvider;
import org.ezhitkevich.cloud_api.properties.JwtProperties;
import org.ezhitkevich.cloud_api.service.security.LogoutService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:cache/cache.properties")
@Slf4j
public class LogoutServiceImpl implements LogoutService {

    private final CacheManager cacheManager;

    private final JwtProperties jwtProperties;

    private final JwtProvider jwtProvider;

    @Value("${cache.name}")
    private String cacheName;

    @Override
    public void logout(String header) {
        try {
            log.info("Method logout in class {} started", getClass().getSimpleName());
            String authToken = null;
            if (header != null && header.startsWith(jwtProperties.getTokenType())) {
                authToken = header.substring(jwtProperties.getTokenType().length());
            }
            String login = jwtProvider.getLogin(authToken);
            cacheManager.getCache(cacheName).put(login, authToken);
            log.info("Method logout in class {} finished", getClass().getSimpleName());
        } catch (Exception e) {
            log.error("Cannot logout. Cause: {}", e.getMessage());
            throw new InvalidJwtTokenException();
        }
    }
}
