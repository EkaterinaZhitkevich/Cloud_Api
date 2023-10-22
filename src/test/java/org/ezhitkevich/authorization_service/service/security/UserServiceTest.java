package org.ezhitkevich.authorization_service.service.security;

import org.ezhitkevich.authorization_service.model.Role;
import org.ezhitkevich.authorization_service.model.User;
import org.ezhitkevich.authorization_service.exception.UserLoginExistsException;
import org.ezhitkevich.authorization_service.jwt.impl.JwtProviderImpl;
import org.ezhitkevich.authorization_service.repository.UserRepository;
import org.ezhitkevich.authorization_service.service.security.impl.RoleServiceImpl;
import org.ezhitkevich.authorization_service.service.security.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    RoleServiceImpl roleService;

    @Mock
    JwtProviderImpl jwtProviderImpl;

    @Mock
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Test
    public void registerShouldSuccessfullyReturnUser() {
        UUID userUuid = UUID.randomUUID();
        User user = User.builder()
                .id(1L)
                .userUuid(userUuid)
                .login("user")
                .password("password")
                .build();
        Role role = new Role("USER");
        String encodedUserPassword = user.getPassword()
                .transform(s -> new StringBuilder(s).reverse().toString());


        when(passwordEncoder.encode(user.getPassword())).thenReturn(encodedUserPassword);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        when(roleService.findRoleByRoleName("USER")).thenReturn(role);
        user.setRoles(Set.of(role));

        when(userRepository.save(any(User.class))).thenReturn(user);
        User registeredUser = userService.register(user);
        verify(userRepository, times(1)).save(user);
        verify(roleService, times(1)).findRoleByRoleName("USER");

        assertThat(registeredUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    public void registerShouldThrowExceptionWhenUserLoginExist() {
        User user = User.builder()
                .id(1L)
                .userUuid(UUID.randomUUID())
                .login("user")
                .password("password")
                .build();

        when(userRepository.existsByLogin(user.getLogin())).thenReturn(true);

        assertThrows(UserLoginExistsException.class, () -> userService.register(user));
    }

    @Test
    public void getAuthorizationTokenShouldSuccessfullyReturnToken() {
        UserDetails user =
                new org.springframework.security.core.userdetails.User("user", "password",
                        List.of(new Role("USER")));

        String expectedToken = "auth-token";

        when(jwtProviderImpl.generateToken(user)).thenReturn(expectedToken);
        String actualToken = userService.getAuthorizationToken(user);

        assertEquals(expectedToken, actualToken);
    }

    @Test
    public void userExistByLoginShouldSuccessfullyReturnTrue(){
        String login = "login";

        when(userRepository.existsByLogin(login)).thenReturn(true);
        boolean result = userService.userExistByLogin(login);

        assertTrue(result);

    }
}
