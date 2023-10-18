package org.ezhitkevich.authorization_service.exception;

public class UserLoginExistsException extends RuntimeException{

    private static final String MESSAGE = "User with login %s is already exists";

    public UserLoginExistsException(String login) {
        super(String.format(MESSAGE, login));
    }

}
