package org.ezhitkevich.authorization_service.facade.security.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ezhitkevich.authorization_service.dto.AuthResponseDto;
import org.ezhitkevich.authorization_service.dto.UserRequestDto;
import org.ezhitkevich.authorization_service.entity.Role;
import org.ezhitkevich.authorization_service.entity.User;
import org.ezhitkevich.authorization_service.exception.UserLoginExistsException;
import org.ezhitkevich.authorization_service.facade.security.AuthorizationFacade;
import org.ezhitkevich.authorization_service.service.security.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Set;

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

    @Override
    public User save(UserRequestDto userRequestDto) {
        log.info("Method save in class {} started", getClass().getSimpleName());

        if (userService.userExistByLogin(userRequestDto.getLogin())){
            throw new UserLoginExistsException(userRequestDto.getLogin());
        }

        User user = User.builder()
                .login(userRequestDto.getLogin())
                .password(userRequestDto.getPassword())
                .roles(Set.of(Role.builder().roleName("USER").build()))
                .build();
        User registeredUser = userService.register(user);

        log.info("Method save in class {} started", getClass().getSimpleName());
        return registeredUser;
    }

}
