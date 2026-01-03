package com.carrilloabogados.n8n.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración para conectar con n8n Cloud.
 */
@Configuration
@ConfigurationProperties(prefix = "n8n.cloud")
public class N8nCloudConfig {

    private String baseUrl = "https://carrilloabgd.app.n8n.cloud";

    private Webhooks webhooks = new Webhooks();

    private int timeoutSeconds = 30;

    private Retry retry = new Retry();

    private boolean enabled = true;

    // Getters and Setters
    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Webhooks getWebhooks() {
        return webhooks;
    }

    public void setWebhooks(Webhooks webhooks) {
        this.webhooks = webhooks;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public Retry getRetry() {
        return retry;
    }

    public void setRetry(Retry retry) {
        this.retry = retry;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * URLs de webhooks en n8n Cloud.
     */
    public static class Webhooks {
        private String leadEvents = "/webhook/lead-events";
        private String meetingEvents = "/webhook/meeting-events";
        private String caseEvents = "/webhook/case-events";

        public String getLeadEvents() {
            return leadEvents;
        }

        public void setLeadEvents(String leadEvents) {
            this.leadEvents = leadEvents;
        }

        public String getMeetingEvents() {
            return meetingEvents;
        }

        public void setMeetingEvents(String meetingEvents) {
            this.meetingEvents = meetingEvents;
        }

        public String getCaseEvents() {
            return caseEvents;
        }

        public void setCaseEvents(String caseEvents) {
            this.caseEvents = caseEvents;
        }

        public String getFullLeadEventsUrl(String baseUrl) {
            return baseUrl + leadEvents;
        }
    }

    /**
     * Configuración de reintentos.
     */
    public static class Retry {
        private int maxAttempts = 3;
        private int delayMillis = 1000;

        public int getMaxAttempts() {
            return maxAttempts;
        }

        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        public int getDelayMillis() {
            return delayMillis;
        }

        public void setDelayMillis(int delayMillis) {
            this.delayMillis = delayMillis;
        }
    }
}
