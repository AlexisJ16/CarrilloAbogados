package com.carrilloabogados.n8n.config;

import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

/**
 * Configuración de RestTemplate para comunicación con n8n Cloud.
 */
@Configuration
public class RestTemplateConfig {

    private final N8nCloudConfig n8nConfig;

    public RestTemplateConfig(N8nCloudConfig n8nConfig) {
        this.n8nConfig = n8nConfig;
    }

    @Bean
    public RestTemplate n8nRestTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(n8nConfig.getTimeoutSeconds()))
                .setReadTimeout(Duration.ofSeconds(n8nConfig.getTimeoutSeconds()))
                .additionalInterceptors(loggingInterceptor())
                .build();
    }

    private ClientHttpRequestInterceptor loggingInterceptor() {
        return (request, body, execution) -> {
            org.slf4j.LoggerFactory.getLogger("N8nClient")
                    .info("Sending request to n8n: {} {}", request.getMethod(), request.getURI());
            var response = execution.execute(request, body);
            org.slf4j.LoggerFactory.getLogger("N8nClient")
                    .info("Received response from n8n: {}", response.getStatusCode());
            return response;
        };
    }
}
