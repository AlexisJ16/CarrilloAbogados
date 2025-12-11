package com.carrilloabogados.legalcase.exception.wrapper;

public class LegalCaseNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public LegalCaseNotFoundException() {
		super();
	}
	
	public LegalCaseNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public LegalCaseNotFoundException(String message) {
		super(message);
	}
	
	public LegalCaseNotFoundException(Throwable cause) {
		super(cause);
	}
	
}