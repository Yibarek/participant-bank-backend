package org.multimedia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "User login credentials payload")
public class LoginRequest {

    @NotBlank(message = "Username is required")
    @Schema(description = "Username for authentication", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "Password is required")
    @Schema(description = "Password for authentication", example = "Password@123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "OAuth2 grant type", example = "password", defaultValue = "password")
    @Builder.Default
    private String grant_type = "password";

    @Schema(description = "Keycloak client ID", example = "acs_apis_client_api", defaultValue = "acs_apis_client_api")
    @Builder.Default
    private String client_id = "acs_apis_client_api";
}
