package org.ezhitkevich.authorization_service.facade.security.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ezhitkevich.authorization_service.dto.UserRequestDto;
import org.ezhitkevich.authorization_service.exception.UserLoginExistsException;
import org.ezhitkevich.authorization_service.facade.security.RegistrationFacade;
import org.ezhitkevich.authorization_service.model.Role;
import org.ezhitkevich.authorization_service.model.User;
import org.ezhitkevich.authorization_service.service.security.UserService;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegistrationFacadeImpl implements RegistrationFacade {

    private final UserService userService;
    @Override
    public void register(UserRequestDto userRequestDto) {
       log.info("Method register in class {} started", getClass().getSimpleName());

        if (userService.userExistByLogin(userRequestDto.getLogin())){
            throw new UserLoginExistsException(userRequestDto.getLogin());
        }

        User user = User.builder()
                .login(userRequestDto.getLogin())
                .password(userRequestDto.getPassword())
                .roles(Set.of(Role.builder().roleName("USER").build()))
                .build();
        userService.register(user);

     log.info("Method register in class {} finished", getClass().getSimpleName());
    }
}
