package org.multimedia.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${client.participant.logoPath:C:/etswitch/uploads/logos/}")
    private String logoUploadLocation;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD")
                .allowedHeaders("*")
                .exposedHeaders("Authorization", "Content-Disposition")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = logoUploadLocation.replace("\\", "/");
        if (!location.endsWith("/")) {
            location += "/";
        }
        if (!location.startsWith("file:")) {
            location = "file:" + location;
        }

        registry.addResourceHandler("/uploads/logos/**")
                .addResourceLocations(location);
    }
}
