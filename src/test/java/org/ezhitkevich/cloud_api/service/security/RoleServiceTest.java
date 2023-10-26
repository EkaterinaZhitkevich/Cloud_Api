package org.ezhitkevich.cloud_api.service.security;

import org.ezhitkevich.cloud_api.model.Role;
import org.ezhitkevich.cloud_api.exception.RoleNotFoundException;
import org.ezhitkevich.cloud_api.repository.RoleRepository;
import org.ezhitkevich.cloud_api.service.security.impl.RoleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @InjectMocks
    RoleServiceImpl roleService;

    @Mock
    RoleRepository roleRepository;

    @Test
    public void findRoleByRoleNameShouldSuccessfullyReturnRole(){
        String roleName = "USER";
        Role role = new Role("USER");

        when(roleRepository.findByRoleName(roleName)).thenReturn(Optional.of(role));
        Role actualRole = roleService.findRoleByRoleName(roleName);

        assertEquals(role, actualRole);
    }

    @Test
    public void findRoleByNameShouldThrowExceptionWhenRoleNotFound(){
        String roleName = "USER";

        when(roleRepository.findByRoleName(roleName)).thenThrow(RoleNotFoundException.class);

        assertThrows(RoleNotFoundException.class, () -> roleService.findRoleByRoleName(roleName));
    }

}
