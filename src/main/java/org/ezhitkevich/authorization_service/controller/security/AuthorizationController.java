package org.ezhitkevich.authorization_service.controller.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ezhitkevich.authorization_service.dto.AuthResponseDto;
import org.ezhitkevich.authorization_service.dto.UserRequestDto;
import org.ezhitkevich.authorization_service.facade.security.AuthorizationFacade;
import org.ezhitkevich.authorization_service.service.security.LogoutService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cloud")
@RequiredArgsConstructor
@Slf4j
public class AuthorizationController {

    private final AuthorizationFacade authorizationFacade;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequestDto userRequestDto) {
        log.info("Method login in class {} started", getClass().getSimpleName());

        AuthResponseDto authorizationToken = authorizationFacade.login(userRequestDto);

        log.info("Method login in class {} finished", getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.OK).body(authorizationToken);
    }


}

