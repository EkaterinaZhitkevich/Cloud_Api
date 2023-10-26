package org.ezhitkevich.cloud_api.facade.security.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ezhitkevich.cloud_api.dto.UserRequestDto;
import org.ezhitkevich.cloud_api.exception.UserLoginExistsException;
import org.ezhitkevich.cloud_api.facade.security.RegistrationFacade;
import org.ezhitkevich.cloud_api.model.Role;
import org.ezhitkevich.cloud_api.model.User;
import org.ezhitkevich.cloud_api.service.security.UserService;
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
