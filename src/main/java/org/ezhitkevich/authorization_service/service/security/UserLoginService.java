package org.ezhitkevich.authorization_service.service.security;

import org.ezhitkevich.authorization_service.model.User;

public interface UserLoginService {

    User findUserByLogin(String login);
}
