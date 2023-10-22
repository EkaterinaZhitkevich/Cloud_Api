package org.ezhitkevich.authorization_service.exception;

public class NoFilesFoundException extends CustomException {

    private static final int EXCEPTION_ID = 5;

    private static final String MESSAGE = "No files for user %s found";

    public NoFilesFoundException(String login) {
        super(String.format(MESSAGE, login));
    }

    @Override
    public int getId() {
        return EXCEPTION_ID;
    }
}
