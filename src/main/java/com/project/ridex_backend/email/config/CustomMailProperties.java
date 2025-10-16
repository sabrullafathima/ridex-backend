package com.project.ridex_backend.email.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "mail")
public class CustomMailProperties {
    private String host;
    private int port;
    private String username;
    private String password;
    private String from;
}
