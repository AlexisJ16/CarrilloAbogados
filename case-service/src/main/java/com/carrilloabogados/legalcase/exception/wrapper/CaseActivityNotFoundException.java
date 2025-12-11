package com.carrilloabogados.legalcase.exception.wrapper;

public class CaseActivityNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public CaseActivityNotFoundException() {
		super();
	}
	
	public CaseActivityNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public CaseActivityNotFoundException(String message) {
		super(message);
	}
	
	public CaseActivityNotFoundException(Throwable cause) {
		super(cause);
	}
	
}