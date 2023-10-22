package org.ezhitkevich.authorization_service.service.security;

import org.ezhitkevich.authorization_service.model.User;
import org.ezhitkevich.authorization_service.exception.UserNotFoundException;
import org.ezhitkevich.authorization_service.repository.UserRepository;
import org.ezhitkevich.authorization_service.service.security.impl.UserLoginServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserLoginServiceTest {

    @InjectMocks
    UserLoginServiceImpl userLoginService;

    @Mock
    UserRepository userRepository;

    @Test
    public void findUserByLoginShouldSuccessfullyReturnUser(){
        String login = "user";
        User user = User.builder()
                .id(1L)
                .userUuid(UUID.randomUUID())
                .login("user")
                .password("password")
                .build();

        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));
        User actualUser = userLoginService.findUserByLogin(login);
        verify(userRepository, times(1)).findByLogin(login);

        Assertions.assertEquals(user, actualUser);
    }

    @Test
    public void findUserByLoginShouldThrowExceptionWhenUserNotFound(){
        String login = "user";

        when(userRepository.findByLogin(login)).thenThrow(UserNotFoundException.class);

        Assertions.assertThrows(UserNotFoundException.class, () -> userLoginService.findUserByLogin(login));
    }

}
