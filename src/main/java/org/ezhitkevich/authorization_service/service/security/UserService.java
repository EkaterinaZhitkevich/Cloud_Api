package org.ezhitkevich.authorization_service.service.security;

import org.ezhitkevich.authorization_service.model.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface UserService {

    User register(User user);

    String getAuthorizationToken(UserDetails user);

    boolean userExistByLogin(String login);

    User findUserByLogin(String login);
}
