package org.ezhitkevich.authorization_service.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ezhitkevich.authorization_service.entity.Role;
import org.ezhitkevich.authorization_service.exception.InvalidJwtTokenException;
import org.ezhitkevich.authorization_service.properties.JwtProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    private final JwtProperties jwtProperties;

    public String generateToken(UserDetails user) {
        List<String> roles
                = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return JWT.create()
                .withSubject(user.getUsername())
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
