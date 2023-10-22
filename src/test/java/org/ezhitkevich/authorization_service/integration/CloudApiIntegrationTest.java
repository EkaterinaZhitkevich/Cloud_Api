package org.ezhitkevich.authorization_service.integration;

import org.ezhitkevich.authorization_service.facade.files.FilesFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@WithMockUser
public class CloudApiIntegrationTest extends AbstractIntegrationTest{

    @Autowired
    MockMvc mockMvc;

    @Autowired
    FilesFacade filesFacade;

    @Test
    public void getAllFilesShouldSuccessfullyReturnOkAndListFileResponseDto(){

    }

}
