package org.ezhitkevich.cloud_api.exception;

public class UserNotFoundException extends CustomException{

    private static final int EXCEPTION_ID = 5;

    private static final String MESSAGE = "User with login %s not found";

    public UserNotFoundException(String login) {
        super(String.format(MESSAGE, login));
    }

    @Override
    public int getId() {
        return EXCEPTION_ID;
    }
}
