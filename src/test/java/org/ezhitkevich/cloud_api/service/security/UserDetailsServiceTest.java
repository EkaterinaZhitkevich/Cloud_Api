package org.ezhitkevich.cloud_api.service.security;

import org.ezhitkevich.cloud_api.model.Role;
import org.ezhitkevich.cloud_api.model.User;
import org.ezhitkevich.cloud_api.service.security.impl.UserDetailsServiceImpl;
import org.ezhitkevich.cloud_api.service.security.impl.UserLoginServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceTest {

    @InjectMocks
    UserDetailsServiceImpl userDetailsService;

    @Mock
    UserLoginServiceImpl userLoginService;

    @Test
    public void loadUserByUsernameShouldSuccessfullyReturnUserDetails(){
        String username = "user";
        Role role = new Role("USER");

        User user = User.builder()
                .id(1L)
                .userUuid(UUID.randomUUID())
                .login("user")
                .password("password")
                .roles(Set.of(role))
                .build();

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getLogin())
                .password(user.getPassword())
                .authorities(user.getRoles())
                .build();

        Mockito.when(userLoginService.findUserByLogin(username)).thenReturn(user);
        UserDetails actualUserDetails = userDetailsService.loadUserByUsername(username);

        Assertions.assertEquals(userDetails, actualUserDetails);
    }
}
