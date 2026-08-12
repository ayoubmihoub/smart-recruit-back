package com.irrigo.userservice;

import com.irrigo.userservice.service.KeycloakService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceApplicationTests {

    @MockitoBean
    private KeycloakService keycloakService;

    @Test
    void contextLoads() {
    }

}
