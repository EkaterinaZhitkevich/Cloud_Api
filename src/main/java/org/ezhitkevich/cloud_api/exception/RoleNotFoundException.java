package org.ezhitkevich.cloud_api.exception;

public class RoleNotFoundException extends CustomException{

    private static final int EXCEPTION_ID = 3;

    private static final String MESSAGE = "Role with name %s not found";
    public RoleNotFoundException(String roleName) {
        super(String.format(MESSAGE, roleName));
    }

    @Override
    public int getId() {
        return EXCEPTION_ID;
    }
}
