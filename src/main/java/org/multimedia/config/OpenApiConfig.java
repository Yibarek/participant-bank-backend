package org.multimedia.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI participantOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Participant Bank Management API (Auth Required - branch3-auth-with-logo)")
                        .description("""
                                REST API for Participant Bank Management and Authentication.
                                
                                Branch: `branch3-auth-with-logo` (Access Token REQUIRED)
                                - Protected endpoints require a valid Keycloak JWT Access Token passed in the `Authorization: Bearer <token>` header.
                                - How to authenticate in Swagger UI:
                                  1. Send a POST request to `/api/v1/auth/login` with your credentials (`admin` / `Password@123`).
                                  2. Copy the returned `access_token` from the response.
                                  3. Click the 'Authorize' (padlock) button at the top right of this Swagger page.
                                  4. Paste your token and click 'Authorize'.
                                - Logo Management: Supports uploading logos via `POST /api/v1/participants/{id}/logo` and static retrieval via `/uploads/logos/{filename}`.
                                """)
                        .version("1.0.0-branch3")
                        .contact(new Contact()
                                .name("EthSwitch Multimedia Team")
                                .url("https://epg.ethswitch.et"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://epg.ethswitch.et"))
                )
                .components(new Components()
                        .addSecuritySchemes(
                                SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Keycloak issued JWT access token. Obtain one via the /api/v1/auth/login endpoint.")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }
}
