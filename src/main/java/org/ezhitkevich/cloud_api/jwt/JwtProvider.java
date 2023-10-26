package org.ezhitkevich.cloud_api.jwt;

import com.auth0.jwt.interfaces.Claim;
import org.ezhitkevich.cloud_api.model.Role;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;

public interface JwtProvider {

    String generateToken(UserDetails user);

    boolean validateToken(String token);

    Map<String, Claim> getClaims(String token);

    String getLogin(String token);

    List<Role> getRoles(String token);

}
