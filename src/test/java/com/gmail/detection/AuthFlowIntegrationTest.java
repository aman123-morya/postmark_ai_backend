package com.gmail.detection;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.detection.dto.LoginRequest;
import com.gmail.detection.dto.RegisterRequest;
import com.gmail.detection.enums.DepartmentType;
import com.gmail.detection.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end verification of the JWT auth flow against the real H2 database:
 * register -> login -> access a protected endpoint -> refresh -> logout ->
 * confirm the old token no longer works. Also checks that a non-admin user is
 * correctly blocked from admin-only endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void adminCanLoginAndAccessAdminEndpoints() throws Exception {

        LoginRequest login = new LoginRequest();
        login.setEmail("test-admin@gmail.com");
        login.setPassword("TestAdmin@123");

        String responseJson = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String accessToken = objectMapper.readTree(responseJson).get("token").asText();
        String refreshToken = objectMapper.readTree(responseJson).get("refreshToken").asText();

        // Admin-only endpoint should be reachable with the access token.
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        // Refresh should mint a new working access token.
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType("application/json")
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());

        // Logout should invalidate the original access token.
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void regularUserIsBlockedFromAdminOnlyEndpoints() throws Exception {

        RegisterRequest register = new RegisterRequest();
        register.setFirstName("Regular");
        register.setLastName("User");
        register.setEmail("employee@gmail.com");
        register.setPassword("Employee@123");
        register.setRole(Role.EMPLOYEE);
        register.setDepartment(DepartmentType.SUPPORT);

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        LoginRequest login = new LoginRequest();
        login.setEmail("employee@gmail.com");
        login.setPassword("Employee@123");

        String responseJson = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String accessToken = objectMapper.readTree(responseJson).get("token").asText();

        // Non-admin should be able to hit a normal authenticated endpoint...
        mockMvc.perform(get("/api/dashboard")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        // ...but not an admin-only one.
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());
    }
}
