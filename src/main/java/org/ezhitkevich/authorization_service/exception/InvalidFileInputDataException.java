package org.ezhitkevich.authorization_service.exception;

public class InvalidFileInputDataException extends CustomException{

    private static final int EXCEPTION_ID = 1;

    @Override
    public int getId() {
        return EXCEPTION_ID;
    }
}
