package org.ezhitkevich.authorization_service.service.security;

import org.ezhitkevich.authorization_service.model.Role;

public interface RoleService {

    Role findRoleByRoleName(String roleName);
}
