package org.ezhitkevich.cloud_api.exception;

public class UserLoginExistsException extends CustomException{

    private static final int EXCEPTION_ID = 4;

    private static final String MESSAGE = "User with login %s is already exists";

    public UserLoginExistsException(String login) {
        super(String.format(MESSAGE, login));
    }
    @Override
    public int getId() {
        return EXCEPTION_ID;
    }

}
