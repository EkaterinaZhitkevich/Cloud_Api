package org.ezhitkevich.authorization_service.exception;

public class UserNotFoundException extends RuntimeException{
    private static final String MESSAGE = "User with login %s not found";

    public UserNotFoundException(String login) {
        super(String.format(MESSAGE, login));
    }
}
