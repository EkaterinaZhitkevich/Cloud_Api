package org.ezhitkevich.authorization_service.service.security;

import jakarta.servlet.http.HttpServletRequest;

public interface LogoutService {

    void logout(String header);
}
