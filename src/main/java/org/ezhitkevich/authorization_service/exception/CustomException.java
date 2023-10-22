package org.ezhitkevich.authorization_service.exception;

public abstract class CustomException extends RuntimeException{

    public abstract int getId();

    public CustomException(String message) {
        super(message);
    }

    public CustomException() {
    }
}
