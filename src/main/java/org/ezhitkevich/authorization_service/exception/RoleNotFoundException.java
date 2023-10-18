package org.ezhitkevich.authorization_service.exception;

public class RoleNotFoundException extends RuntimeException{

    private static final String MESSAGE = "Role with name %s not found";
    public RoleNotFoundException(String roleName) {
        super(String.format(MESSAGE, roleName));
    }
}
