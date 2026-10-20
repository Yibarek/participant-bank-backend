package org.multimedia;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.multimedia.dto.LoginRequest;
import org.multimedia.dto.ParticipantBankDto;
import org.multimedia.model.ParticipantBank;
import org.multimedia.repository.ParticipantBankRepository;
import org.multimedia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ParticipantBankControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ParticipantBankRepository participantBankRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        participantBankRepository.deleteAll();
        userRepository.deleteAll();

        Map<String, Object> keycloakResponse = new HashMap<>();
        keycloakResponse.put("access_token", "test_keycloak_jwt_token");
        keycloakResponse.put("refresh_token", "test_keycloak_refresh_token");
        keycloakResponse.put("token_type", "Bearer");
        keycloakResponse.put("expires_in", 300);
        keycloakResponse.put("refresh_expires_in", 1800);
        keycloakResponse.put("scope", "openid profile email");

        Mockito.when(restTemplate.postForEntity(
                anyString(),
                org.mockito.ArgumentMatchers.any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(ResponseEntity.ok(keycloakResponse));
    }

    @Test
    public void testPublicLoginEndpointSucceedsWithoutToken() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .username("admin")
                .password("Password@123")
                .grant_type("password")
                .client_id("acs_apis_client_api")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token", is("test_keycloak_jwt_token")))
                .andExpect(jsonPath("$.token_type", is("Bearer")));
    }

    @Test
    public void testLoginFailsWhenKeycloakFailsWithoutFallback() throws Exception {
        Mockito.when(restTemplate.postForEntity(
                anyString(),
                org.mockito.ArgumentMatchers.any(HttpEntity.class),
                eq(Map.class)
        )).thenThrow(new RestClientException("Connection refused to authentication server"));

        LoginRequest loginRequest = LoginRequest.builder()
                .username("admin")
                .password("Password@123")
                .grant_type("password")
                .client_id("acs_apis_client_api")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testProtectedEndpointRejectsUnauthenticatedRequestsWith401() throws Exception {
        mockMvc.perform(get("/api/v1/participants")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.error", is("Unauthorized")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testProtectedEndpointAllowsAuthenticatedUser() throws Exception {
        ParticipantBankDto dto = ParticipantBankDto.builder()
                .name("Commercial Bank of Ethiopia")
                .bic("CBETETAA")
                .shortName("CBE")
                .address("Sudan Street, Addis Ababa")
                .email("contact@cbe.com.et")
                .phoneNumber("+251111234567")
                .status("ACTIVE")
                .build();

        // 1. Add Bank with authentication
        mockMvc.perform(post("/api/v1/participants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.institution.name", is("Commercial Bank of Ethiopia")));

        // 2. Retrieve All Banks
        mockMvc.perform(get("/api/v1/participants")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.institutions", hasSize(1)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUploadLogoWithAuthentication() throws Exception {
        ParticipantBank bank = participantBankRepository.save(
                ParticipantBank.builder()
                        .name("Awash Bank")
                        .bic("AWINETAA")
                        .shortName("AB")
                        .status("ACTIVE")
                        .build()
        );

        org.springframework.mock.web.MockMultipartFile logoFile =
                new org.springframework.mock.web.MockMultipartFile(
                        "logo",
                        "awash_logo.png",
                        "image/png",
                        "dummy-png-data".getBytes()
                );

        mockMvc.perform(multipart("/api/v1/participants/" + bank.getId() + "/logo")
                        .file(logoFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.institution.logoPath", containsString("AWINETAA_logo.png")));
    }
}
