package com.carrilloabogados.legalcase.exception.wrapper;

public class CaseTypeNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public CaseTypeNotFoundException() {
		super();
	}
	
	public CaseTypeNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public CaseTypeNotFoundException(String message) {
		super(message);
	}
	
	public CaseTypeNotFoundException(Throwable cause) {
		super(cause);
	}
	
}