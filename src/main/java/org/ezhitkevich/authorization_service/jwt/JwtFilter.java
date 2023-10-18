package org.ezhitkevich.authorization_service.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ezhitkevich.authorization_service.entity.Role;
import org.ezhitkevich.authorization_service.exception.InvalidJwtTokenException;
import org.ezhitkevich.authorization_service.properties.JwtProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@PropertySource("classpath:cache/cache.properties")
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    private final CacheManager cacheManager;

    private final JwtProperties jwtProperties;

    @Value("${cache.name}")
    private String cacheName;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("Jwt filter processing started");
        try {

            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (authHeader != null && authHeader.startsWith(jwtProperties.getTokenType())) {
                String jwt = authHeader.substring(jwtProperties.getTokenType().length());

                if (jwtProvider.validateToken(jwt)) {

                    if (cacheManager.getCache(cacheName).get(jwtProvider.getLogin(jwt), String.class) != null) {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    }
                    String login = jwtProvider.getLogin(jwt);
                    List<Role> roles = jwtProvider.getRoles(jwt);

                    Authentication authentication =
                            new PreAuthenticatedAuthenticationToken(login,
                                    null, roles);

                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }

                }
            }
            filterChain.doFilter(request, response);
            log.info("Jwt filter processing finished");
        } catch (Exception e) {
            log.error("Cannot set authentication {}", e.getMessage());
            throw new InvalidJwtTokenException();
        }
    }
}
