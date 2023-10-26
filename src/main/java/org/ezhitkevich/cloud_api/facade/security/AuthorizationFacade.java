package org.ezhitkevich.cloud_api.facade.security;

import org.ezhitkevich.cloud_api.dto.response.AuthResponseDto;
import org.ezhitkevich.cloud_api.dto.request.UserRequestDto;

public interface AuthorizationFacade {

    AuthResponseDto login(UserRequestDto userRequestDto);

}
