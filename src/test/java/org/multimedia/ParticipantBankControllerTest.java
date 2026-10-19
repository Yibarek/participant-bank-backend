package org.multimedia;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.multimedia.dto.LoginRequest;
import org.multimedia.dto.ParticipantBankDto;
import org.multimedia.model.ParticipantBank;
import org.multimedia.repository.ParticipantBankRepository;
import org.multimedia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
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

    @BeforeEach
    public void setUp() {
        participantBankRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testLoginWithStaticCredentials() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .username("admin")
                .password("Password@123")
                .grant_type("password")
                .client_id("participant-portal")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token", notNullValue()))
                .andExpect(jsonPath("$.token_type", is("Bearer")));
    }

    @Test
    public void testCreateAndRetrieveParticipantBank() throws Exception {
        ParticipantBankDto dto = ParticipantBankDto.builder()
                .name("Commercial Bank of Ethiopia")
                .bic("CBETETAA")
                .shortName("CBE")
                .address("Sudan Street, Addis Ababa")
                .email("contact@cbe.com.et")
                .phoneNumber("+251111234567")
                .status("ACTIVE")
                .build();

        // 1. Add Bank
        mockMvc.perform(post("/api/v1/participants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.institution.name", is("Commercial Bank of Ethiopia")))
                .andExpect(jsonPath("$.institution.bic", is("CBETETAA")));

        // 2. Get All Banks
        mockMvc.perform(get("/api/v1/participants")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.institutions", hasSize(1)))
                .andExpect(jsonPath("$.institutions[0].bic", is("CBETETAA")));

        // 3. Get Bank by BIC
        mockMvc.perform(get("/api/v1/participants/bic/CBETETAA")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Commercial Bank of Ethiopia")));

        // 4. Search Bank
        mockMvc.perform(get("/api/v1/participants/search?query=CBE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.institutions", hasSize(1)));
    }

    @Test
    public void testUploadParticipantLogo() throws Exception {
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
