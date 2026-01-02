package com.carrilloabogados.client.exception.wrapper;

import java.io.Serial;

/**
 * Excepción lanzada cuando no se encuentra un Lead.
 */
public class LeadNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public LeadNotFoundException() {
        super();
    }

    public LeadNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public LeadNotFoundException(String message) {
        super(message);
    }

    public LeadNotFoundException(Throwable cause) {
        super(cause);
    }

}
