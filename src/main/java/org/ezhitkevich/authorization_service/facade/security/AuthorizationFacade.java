package org.ezhitkevich.authorization_service.facade.security;

import org.ezhitkevich.authorization_service.dto.AuthResponseDto;
import org.ezhitkevich.authorization_service.dto.UserRequestDto;
import org.ezhitkevich.authorization_service.entity.User;

public interface AuthorizationFacade {

    AuthResponseDto login(UserRequestDto userRequestDto);

    User save(UserRequestDto userRequestDto);
}
