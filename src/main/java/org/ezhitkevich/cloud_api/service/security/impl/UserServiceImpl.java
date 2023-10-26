package org.ezhitkevich.cloud_api.service.security.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ezhitkevich.cloud_api.exception.UserLoginExistsException;
import org.ezhitkevich.cloud_api.exception.UserNotFoundException;
import org.ezhitkevich.cloud_api.jwt.JwtProvider;
import org.ezhitkevich.cloud_api.model.Role;
import org.ezhitkevich.cloud_api.model.User;
import org.ezhitkevich.cloud_api.repository.UserRepository;
import org.ezhitkevich.cloud_api.service.security.RoleService;
import org.ezhitkevich.cloud_api.service.security.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final JwtProvider jwtProvider;

    private final RoleService roleService;

    @Override
    @Transactional
    public User register(User user) {
        log.info("Method register in class {} started", getClass().getSimpleName());

        if (userRepository.existsByLogin(user.getLogin())) {
            throw new UserLoginExistsException(user.getLogin());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Set<Role> roles = user.getRoles().stream()
                .map(role -> roleService.findRoleByRoleName(role.getRoleName()))
                .collect(Collectors.toSet());
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        log.info("Method register in class {} finished", getClass().getSimpleName());
        return savedUser;
    }

    @Override
    @Transactional
    public String getAuthorizationToken(UserDetails user) {
        log.info("Method get authorization token in class {} started", getClass().getSimpleName());

        String token = jwtProvider.generateToken(user);

        log.info("Method get authorization token in class {} finished", getClass().getSimpleName());
        return token;
    }

    @Override
    @Transactional
    public boolean userExistByLogin(String login) {
        return userRepository.existsByLogin(login);
    }

    @Override
    public User findUserByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException(login));
    }
}
