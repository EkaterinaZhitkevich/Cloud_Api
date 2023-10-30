package org.ezhitkevich.cloud_api.jwt.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ezhitkevich.cloud_api.jwt.JwtProvider;
import org.ezhitkevich.cloud_api.model.Role;
import org.ezhitkevich.cloud_api.exception.InvalidJwtTokenException;
import org.ezhitkevich.cloud_api.model.User;
import org.ezhitkevich.cloud_api.properties.JwtProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProviderImpl implements JwtProvider {

    private final JwtProperties jwtProperties;

    public String generateToken(User user) {
        List<String> roles
                = user.getRoles().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return JWT.create()
                .withSubject(user.getLogin())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getLifetime().toMillis()))
                .withClaim("roles", roles)
                .sign(Algorithm.HMAC256(jwtProperties.getSecret()));
    }

    public boolean validateToken(String token) {
        try {
            Verification require = JWT.require(Algorithm.HMAC256(jwtProperties.getSecret()));
            DecodedJWT decode = JWT.decode(token);
            return true;
        } catch (RuntimeException e) {
            log.error("Invalid token {}", e.getMessage());
            throw new InvalidJwtTokenException();
        }
    }

    public Map<String, Claim> getClaims(String token) {
        return JWT.decode(token).getClaims();
    }

    public String getLogin(String token) {
        return JWT.decode(token).getSubject();
    }

    public List<Role> getRoles(String token) {
        Claim roles = JWT.decode(token).getClaims().get("roles");
        List<Role> list = roles.asList(Role.class);
        return list;
    }

}
