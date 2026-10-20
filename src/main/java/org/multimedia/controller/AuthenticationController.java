package org.multimedia.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.multimedia.dto.LoginRequest;
import org.multimedia.dto.TokenResponseDto;
import org.multimedia.service.AuthenticationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = {"/acs-dev/api/v1", "/api/v1/auth"})
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "User authentication and Keycloak token acquisition endpoints")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(
            summary = "User Login",
            description = "Authenticates user with static credentials, registers/updates the user record in the PostgreSQL database, and returns access and refresh tokens from Keycloak."
    )
    @RequestBody(
            description = "JSON credentials payload",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = LoginRequest.class),
                    examples = @ExampleObject(
                            name = "Login Example",
                            value = "{\"username\": \"admin\", \"password\": \"Password@123\", \"grant_type\": \"password\", \"client_id\": \"acs_apis_client_api\"}"
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TokenResponseDto.class),
                            examples = @ExampleObject(
                                    name = "Success Response",
                                    value = "{\"access_token\": \"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...\", \"expires_in\": 300, \"refresh_expires_in\": 1800, \"refresh_token\": \"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...\", \"token_type\": \"Bearer\", \"scope\": \"openid profile email\"}"
                            )
                    )
            )
    })
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponseDto> login(@Valid @org.springframework.web.bind.annotation.RequestBody LoginRequest loginRequest) {
        log.info("Received login request for user: {}", loginRequest.getUsername());
        TokenResponseDto response = authenticationService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Obtain Access Token",
            description = "Alternative endpoint to exchange credentials for a Keycloak JWT token."
    )
    @RequestBody(
            description = "JSON credentials payload",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = LoginRequest.class),
                    examples = @ExampleObject(
                            name = "Token Request Example",
                            value = "{\"username\": \"admin\", \"password\": \"Password@123\", \"grant_type\": \"password\", \"client_id\": \"acs_apis_client_api\"}"
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Token issued successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TokenResponseDto.class),
                            examples = @ExampleObject(
                                    name = "Success Response",
                                    value = "{\"access_token\": \"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...\", \"expires_in\": 300, \"refresh_expires_in\": 1800, \"refresh_token\": \"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...\", \"token_type\": \"Bearer\", \"scope\": \"openid profile email\"}"
                            )
                    )
            )
    })
    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponseDto> token(@Valid @org.springframework.web.bind.annotation.RequestBody LoginRequest loginRequest) {
        log.info("Received token request for user: {}", loginRequest.getUsername());
        TokenResponseDto response = authenticationService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
}
