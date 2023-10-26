package org.ezhitkevich.cloud_api.service.security;

import org.ezhitkevich.cloud_api.model.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {

    User register(User user);

    String getAuthorizationToken(UserDetails user);

    boolean userExistByLogin(String login);

    User findUserByLogin(String login);
}
