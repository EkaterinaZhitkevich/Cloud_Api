package org.ezhitkevich.cloud_api.facade.security.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ezhitkevich.cloud_api.dto.response.AuthResponseDto;
import org.ezhitkevich.cloud_api.dto.request.UserRequestDto;
import org.ezhitkevich.cloud_api.facade.security.AuthorizationFacade;
import org.ezhitkevich.cloud_api.service.security.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationFacadeImpl implements AuthorizationFacade {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;


    @Override
    public AuthResponseDto login(UserRequestDto userRequestDto) {
        log.info("Method login in class {} started", getClass().getSimpleName());
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userRequestDto.getLogin(), userRequestDto.getPassword()));

        UserDetails user = (UserDetails) authenticate.getPrincipal();

        AuthResponseDto authResponseDto = new AuthResponseDto(userService.getAuthorizationToken(user));

        log.info("Method login in class {} finished", getClass().getSimpleName());
        return authResponseDto;
    }


}
