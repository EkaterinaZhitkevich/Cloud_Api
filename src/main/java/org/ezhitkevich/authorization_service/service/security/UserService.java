package org.ezhitkevich.authorization_service.service.security;

import org.ezhitkevich.authorization_service.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {

    User register(User user);

    String getAuthorizationToken(UserDetails user);

    boolean userExistByLogin(String login);
}
