package com.carrilloabogados.notification.domain;

public enum NotificationType {
    // Lead related
    LEAD_CAPTURED,
    LEAD_HOT,
    LEAD_ASSIGNED,

    // Case related
    CASE_CREATED,
    CASE_UPDATED,
    CASE_STATUS_CHANGED,
    CASE_ASSIGNED,

    // Calendar related
    APPOINTMENT_SCHEDULED,
    APPOINTMENT_REMINDER,
    DEADLINE_REMINDER,

    // Document related
    DOCUMENT_UPLOADED,
    DOCUMENT_SHARED,

    // Payment related
    PAYMENT_RECEIVED,
    PAYMENT_DUE,

    // System
    SYSTEM_ALERT,
    WELCOME,
    PASSWORD_RESET
}
