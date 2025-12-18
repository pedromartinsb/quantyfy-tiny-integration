package com.example.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "tiny.api")
@Getter
@Setter
public class TinyProperties {

    private String baseUrl;
    private String token;
    private RateLimit rateLimit = new RateLimit();
    private int pageSize = 100;

    @Getter
    @Setter
    public static class RateLimit {
        private int requestsPerSecond = 5;
    }
}
