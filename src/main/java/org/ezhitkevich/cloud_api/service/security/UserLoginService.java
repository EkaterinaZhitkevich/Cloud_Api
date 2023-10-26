package org.ezhitkevich.cloud_api.service.security;

import org.ezhitkevich.cloud_api.model.User;

public interface UserLoginService {

    User findUserByLogin(String login);
}
