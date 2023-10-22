package org.ezhitkevich.authorization_service.handler;

import lombok.extern.slf4j.Slf4j;
import org.ezhitkevich.authorization_service.dto.CustomExceptionResponse;
import org.ezhitkevich.authorization_service.exception.CustomException;
import org.ezhitkevich.authorization_service.exception.InvalidFileInputDataException;
import org.ezhitkevich.authorization_service.exception.InvalidJwtTokenException;
import org.ezhitkevich.authorization_service.exception.NoFilesFoundException;
import org.ezhitkevich.authorization_service.exception.UserLoginExistsException;
import org.ezhitkevich.authorization_service.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.ConnectException;

@RestControllerAdvice
@Slf4j
public class GlobalAppExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<CustomExceptionResponse> badCredentialsExceptionHandler(BadCredentialsException e) {
        log.warn(e.getMessage());
        log.trace(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(handle(e));
    }

    @ExceptionHandler({UserLoginExistsException.class, UserNotFoundException.class})
    public ResponseEntity<CustomExceptionResponse> userLoginExistExceptionHandler(UserLoginExistsException e) {
        log.warn(e.getMessage());
        log.trace(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(handle(e));
    }

    @ExceptionHandler(NoFilesFoundException.class)
    public ResponseEntity<CustomExceptionResponse> noFilesFoundExceptionHandler(NoFilesFoundException e){
        log.warn(e.getMessage());
        log.trace(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(handle(e));
    }

    @ExceptionHandler(InvalidFileInputDataException.class)
    public ResponseEntity<CustomExceptionResponse> minioExceptionHandler(InvalidFileInputDataException e) {
        log.warn(e.getMessage());
        log.trace(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(handle(e));
    }

    @ExceptionHandler(ConnectException.class)
    public ResponseEntity<CustomExceptionResponse> connectExceptionHandler(ConnectException e) {
        log.warn(e.getMessage());
        log.trace(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(handle(e));
    }

    @ExceptionHandler(InvalidJwtTokenException.class)
    public ResponseEntity<CustomExceptionResponse> invalidJwtTokenExceptionHandler(InvalidJwtTokenException e){
        log.warn(e.getMessage());
        log.trace(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(handle(e));
    }

    private CustomExceptionResponse handle(Exception e){
        CustomExceptionResponse exceptionResponse = null;

        if (e instanceof CustomException){
            CustomException customException = (CustomException) e;
            exceptionResponse = CustomExceptionResponse.builder()
                    .message(customException.getMessage())
                    .id(customException.getId())
                    .build();
        } else {
            exceptionResponse = CustomExceptionResponse.builder()
                    .message(e.getMessage())
                    .id(0)
                    .build();
        }
        return exceptionResponse;
    }
}
