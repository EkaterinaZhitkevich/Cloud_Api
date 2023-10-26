package org.ezhitkevich.cloud_api.service.security;

import org.ezhitkevich.cloud_api.model.Role;

public interface RoleService {

    Role findRoleByRoleName(String roleName);
}
