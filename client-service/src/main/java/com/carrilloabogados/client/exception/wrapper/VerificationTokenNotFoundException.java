package com.carrilloabogados.client.exception.wrapper;

import java.io.Serial;

public class VerificationTokenNotFoundException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1L;
	
	public VerificationTokenNotFoundException() {
		super();
	}
	
	public VerificationTokenNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public VerificationTokenNotFoundException(String message) {
		super(message);
	}
	
	public VerificationTokenNotFoundException(Throwable cause) {
		super(cause);
	}
	
	
	
}










