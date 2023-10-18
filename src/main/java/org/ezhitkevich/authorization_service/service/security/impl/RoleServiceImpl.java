package org.ezhitkevich.authorization_service.service.security.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ezhitkevich.authorization_service.entity.Role;
import org.ezhitkevich.authorization_service.exception.RoleNotFoundException;
import org.ezhitkevich.authorization_service.repository.RoleRepository;
import org.ezhitkevich.authorization_service.service.security.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public Role findRoleByRoleName(String roleName) {
        log.info("Method find role by role name in class {} started", getClass().getSimpleName());
        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RoleNotFoundException(roleName));
        log.info("Method find role by role name in class {} finished", getClass().getSimpleName());
        return role;
    }
}
