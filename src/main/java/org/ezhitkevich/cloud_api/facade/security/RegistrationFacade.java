package org.ezhitkevich.cloud_api.facade.security;

import org.ezhitkevich.cloud_api.dto.UserRequestDto;

public interface RegistrationFacade {

    void register( UserRequestDto userRequestDto);
}
