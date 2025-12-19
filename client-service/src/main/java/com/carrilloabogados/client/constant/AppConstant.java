package com.carrilloabogados.client.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Constantes de aplicación para Client Service - Carrillo Abogados
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class AppConstant {

    public static final String LOCAL_DATE_FORMAT = "dd-MM-yyyy";
    public static final String LOCAL_DATE_TIME_FORMAT = "dd-MM-yyyy__HH:mm:ss:SSSSSS";
    public static final String ZONED_DATE_TIME_FORMAT = "dd-MM-yyyy__HH:mm:ss:SSSSSS";
    public static final String INSTANT_FORMAT = "dd-MM-yyyy__HH:mm:ss:SSSSSS";

    /**
     * URLs de los microservicios para comunicación inter-servicio
     * Usa Spring Cloud Kubernetes para service discovery
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public abstract class DiscoveredDomainsApi {

        // Servicios de negocio legal
        public static final String CLIENT_SERVICE_HOST = "http://CLIENT-SERVICE/client-service";
        public static final String CLIENT_SERVICE_API_URL = "http://CLIENT-SERVICE/client-service/api/clients";

        public static final String CASE_SERVICE_HOST = "http://CASE-SERVICE/case-service";
        public static final String CASE_SERVICE_API_URL = "http://CASE-SERVICE/case-service/api/cases";

        public static final String PAYMENT_SERVICE_HOST = "http://PAYMENT-SERVICE/payment-service";
        public static final String PAYMENT_SERVICE_API_URL = "http://PAYMENT-SERVICE/payment-service/api/payments";

        public static final String DOCUMENT_SERVICE_HOST = "http://DOCUMENT-SERVICE/document-service";
        public static final String DOCUMENT_SERVICE_API_URL = "http://DOCUMENT-SERVICE/document-service/api/documents";

        public static final String CALENDAR_SERVICE_HOST = "http://CALENDAR-SERVICE/calendar-service";
        public static final String CALENDAR_SERVICE_API_URL = "http://CALENDAR-SERVICE/calendar-service/api/events";

        public static final String NOTIFICATION_SERVICE_HOST = "http://NOTIFICATION-SERVICE/notification-service";
        public static final String NOTIFICATION_SERVICE_API_URL = "http://NOTIFICATION-SERVICE/notification-service/api/notifications";

    }

}
