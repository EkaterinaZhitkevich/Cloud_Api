package org.ezhitkevich.cloud_api.exception;

public class InvalidJwtTokenException extends CustomException{

    private static final int EXCEPTION_ID = 2;

    @Override
    public int getId() {
        return EXCEPTION_ID;
    }
}
