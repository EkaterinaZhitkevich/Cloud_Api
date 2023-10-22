package org.ezhitkevich.authorization_service.controller;

import org.ezhitkevich.authorization_service.jwt.impl.JwtFilter;
import org.ezhitkevich.authorization_service.jwt.impl.JwtProviderImpl;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;

public class AbstractControllerTest {

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private JwtProviderImpl jwtProviderImpl;

    @MockBean
    private CacheManager cacheManager;

}
