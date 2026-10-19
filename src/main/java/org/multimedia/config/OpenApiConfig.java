package org.multimedia.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI participantOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Participant Bank Management API (branch2-no-auth-with-logo)")
                        .description("""
                                REST API for Participant Bank Management and Authentication.
                                
                                Branch: `branch2-no-auth-with-logo`
                                - No accessToken is required to test or call any endpoint.
                                - Full Logo Management Enabled: Upload logos via `POST /api/v1/participants/{id}/logo` and retrieve via `/uploads/logos/{filename}`.
                                - Login endpoint authenticates against static credentials, logs into Keycloak, and synchronizes the user to PostgreSQL.
                                """)
                        .version("1.0.0-branch2")
                        .contact(new Contact()
                                .name("EthSwitch Multimedia Team")
                                .url("https://epg.ethswitch.et"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://epg.ethswitch.et"))
                );
    }
}
