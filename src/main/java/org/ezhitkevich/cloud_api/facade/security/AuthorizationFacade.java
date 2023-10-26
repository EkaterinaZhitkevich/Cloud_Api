package org.ezhitkevich.cloud_api.facade.security;

import org.ezhitkevich.cloud_api.dto.AuthResponseDto;
import org.ezhitkevich.cloud_api.dto.UserRequestDto;

public interface AuthorizationFacade {

    AuthResponseDto login(UserRequestDto userRequestDto);

}
