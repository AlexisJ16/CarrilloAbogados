package com.carrilloabogados.n8n.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de URLs de microservicios para callbacks.
 */
@Configuration
@ConfigurationProperties(prefix = "microservices")
public class MicroservicesConfig {

    private ServiceConfig clientService = new ServiceConfig();
    private ServiceConfig notificationService = new ServiceConfig();

    public ServiceConfig getClientService() {
        return clientService;
    }

    public void setClientService(ServiceConfig clientService) {
        this.clientService = clientService;
    }

    public ServiceConfig getNotificationService() {
        return notificationService;
    }

    public void setNotificationService(ServiceConfig notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Obtiene la URL completa del client-service.
     */
    public String getClientServiceUrl() {
        return clientService.getUrl() + clientService.getContextPath();
    }

    /**
     * Obtiene la URL completa del notification-service.
     */
    public String getNotificationServiceUrl() {
        return notificationService.getUrl() + notificationService.getContextPath();
    }

    public static class ServiceConfig {
        private String url = "http://localhost:8200";
        private String contextPath = "";

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getContextPath() {
            return contextPath;
        }

        public void setContextPath(String contextPath) {
            this.contextPath = contextPath;
        }
    }
}
