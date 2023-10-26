package org.ezhitkevich.cloud_api.controller;

import org.ezhitkevich.cloud_api.jwt.impl.JwtFilter;
import org.ezhitkevich.cloud_api.jwt.impl.JwtProviderImpl;
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
