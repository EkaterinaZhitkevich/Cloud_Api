package org.ezhitkevich.authorization_service.service.security.impl;

import lombok.RequiredArgsConstructor;
import org.ezhitkevich.authorization_service.entity.User;
import org.ezhitkevich.authorization_service.exception.UserNotFoundException;
import org.ezhitkevich.authorization_service.repository.UserRepository;
import org.ezhitkevich.authorization_service.service.security.UserLoginService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLoginServiceImpl implements UserLoginService {

    private final UserRepository userRepository;

    @Override
    public User findUserByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException(login));
    }
}
