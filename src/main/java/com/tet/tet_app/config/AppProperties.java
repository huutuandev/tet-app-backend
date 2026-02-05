package com.tet.tet_app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {

    private Cors cors;
    private Cookie cookie;

    @Data
    public static class Cors {
        private List<String> allowedOrigins;
    }

    @Data
    public static class Cookie {
        private boolean secure;
        private String sameSite;
        private String path;
        private long maxAge;
    }
}

