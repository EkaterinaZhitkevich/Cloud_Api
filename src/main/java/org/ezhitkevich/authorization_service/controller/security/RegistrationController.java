package org.ezhitkevich.authorization_service.controller.security;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ezhitkevich.authorization_service.dto.UserRequestDto;
import org.ezhitkevich.authorization_service.facade.security.RegistrationFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cloud")
@RequiredArgsConstructor
@Slf4j
public class RegistrationController {

    private final RegistrationFacade registrationFacade;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid UserRequestDto userRequestDto){
        log.info("Method register in class {} started", getClass().getSimpleName());

        registrationFacade.register(userRequestDto);

        log.info("Method register in class {} finished", getClass().getSimpleName());
        return ResponseEntity.ok().build();
    }


}
