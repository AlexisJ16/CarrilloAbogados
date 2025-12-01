package com.selimhorri.app.exception.wrapper;

import java.io.Serial;

public class IllegalAuthenticationCredentialsException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1L;
	
	public IllegalAuthenticationCredentialsException() {
		super();
	}
	
	public IllegalAuthenticationCredentialsException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public IllegalAuthenticationCredentialsException(String message) {
		super(message);
	}
	
	public IllegalAuthenticationCredentialsException(Throwable cause) {
		super(cause);
	}
	
	
	
}










