package org.ezhitkevich.cloud_api.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class AbstractIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:12-alpine3.18");

    @Container
    static MinIOContainer minioContainer =
            new MinIOContainer("minio/minio:latest")
                    .withUserName("minio_user")
                    .withPassword("minio_password");


    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("minio.url", minioContainer::getS3URL);
        registry.add("minio.access.key", minioContainer::getUserName);
        registry.add("minio.secret.key", minioContainer::getPassword);
    }

    @BeforeAll
    static void beforeAll(){
        postgreSQLContainer.start();
        minioContainer.start();
    }

    @AfterAll
    static void afterAll(){
        postgreSQLContainer.stop();
        minioContainer.stop();
    }

    @Test
    public void containersStartTest(){
        assertTrue(postgreSQLContainer.isCreated());
        assertTrue(minioContainer.isCreated());
        assertTrue(postgreSQLContainer.isRunning());
        assertTrue(minioContainer.isRunning());
    }


}
