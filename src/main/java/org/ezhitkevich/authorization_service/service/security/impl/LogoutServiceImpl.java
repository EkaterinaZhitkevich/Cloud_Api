package org.ezhitkevich.authorization_service.service.security.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ezhitkevich.authorization_service.exception.InvalidJwtTokenException;
import org.ezhitkevich.authorization_service.jwt.JwtProvider;
import org.ezhitkevich.authorization_service.properties.JwtProperties;
import org.ezhitkevich.authorization_service.service.security.LogoutService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
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
