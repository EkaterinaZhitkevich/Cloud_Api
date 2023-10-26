package org.ezhitkevich.cloud_api.exception;

public class InvalidFileInputDataException extends CustomException{

    private static final int EXCEPTION_ID = 1;

    @Override
    public int getId() {
        return EXCEPTION_ID;
    }
}
