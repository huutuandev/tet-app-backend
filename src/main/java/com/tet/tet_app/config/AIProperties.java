package com.tet.tet_app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ai")
@Data
public class AIProperties {

    private String provider;
    private String model;
    private Api api;

    @Data
    public static class Api {
        private String key;
        private String url;
    }
}
