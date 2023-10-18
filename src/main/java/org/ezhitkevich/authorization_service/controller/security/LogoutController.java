package org.ezhitkevich.authorization_service.controller.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ezhitkevich.authorization_service.service.security.LogoutService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cloud")
@Slf4j
public class LogoutController {

    private LogoutService logoutService;

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        log.info("Method logout in class {} started", getClass().getSimpleName());

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        logoutService.logout(header);

        log.info("Method logout in class {} finished", getClass().getSimpleName());
        return ResponseEntity.ok().build();
    }
}
