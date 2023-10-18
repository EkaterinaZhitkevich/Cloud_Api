package org.ezhitkevich.authorization_service.service.security;

import org.ezhitkevich.authorization_service.entity.Role;

public interface RoleService {

    Role findRoleByRoleName(String roleName);
}
