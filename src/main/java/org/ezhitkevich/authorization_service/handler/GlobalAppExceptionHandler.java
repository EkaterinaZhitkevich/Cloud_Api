package org.ezhitkevich.authorization_service.handler;

import lombok.extern.slf4j.Slf4j;
import org.ezhitkevich.authorization_service.dto.CustomResponse;
import org.ezhitkevich.authorization_service.exception.InvalidFileInputDataException;
import org.ezhitkevich.authorization_service.exception.InvalidJwtTokenException;
import org.ezhitkevich.authorization_service.exception.UserLoginExistsException;
import org.ezhitkevich.authorization_service.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.ConnectException;
import java.time.Instant;

@RestControllerAdvice
@Slf4j
public class GlobalAppExceptionHandler {


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<CustomResponse> badCredentialsExceptionHandler(BadCredentialsException e) {
        log.warn(e.getMessage());
        log.trace(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(handle(e));
    }

    @ExceptionHandler({UserLoginExistsException.class, UserNotFoundException.class})
    public ResponseEntity<CustomResponse> userLoginExistExceptionHandler(UserLoginExistsException e) {
        log.warn(e.getMessage());
        log.trace(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(handle(e));
    }

    @ExceptionHandler(InvalidFileInputDataException.class)
    public ResponseEntity<CustomResponse> minioExceptionHandler(InvalidFileInputDataException e) {
        log.warn(e.getMessage());
        log.trace(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(handle(e));
    }

    @ExceptionHandler(ConnectException.class)
    public ResponseEntity<CustomResponse> connectExceptionHandler(ConnectException e) {
        log.warn(e.getMessage());
        log.trace(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(handle(e));
    }

    @ExceptionHandler(InvalidJwtTokenException.class)
    public ResponseEntity<CustomResponse> invalidJwtTokenExceptionHandler(InvalidJwtTokenException e){
        log.warn(e.getMessage());
        log.trace(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(handle(e));
    }


    private CustomResponse handle(Exception e){
        return CustomResponse.builder()
                .instant(Instant.now())
                .message(e.getMessage())
                .build();
    }
}
