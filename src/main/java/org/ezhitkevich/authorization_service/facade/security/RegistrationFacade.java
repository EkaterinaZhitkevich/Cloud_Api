package org.ezhitkevich.authorization_service.facade.security;

import org.ezhitkevich.authorization_service.dto.UserRequestDto;

public interface RegistrationFacade {

    void register( UserRequestDto userRequestDto);
}
