package org.ezhitkevich.cloud_api.service.security.impl;

import lombok.RequiredArgsConstructor;
import org.ezhitkevich.cloud_api.model.User;
import org.ezhitkevich.cloud_api.exception.UserNotFoundException;
import org.ezhitkevich.cloud_api.repository.UserRepository;
import org.ezhitkevich.cloud_api.service.security.UserLoginService;
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
