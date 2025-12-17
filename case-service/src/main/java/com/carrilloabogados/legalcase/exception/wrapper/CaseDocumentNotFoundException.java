package com.carrilloabogados.legalcase.exception.wrapper;

public class CaseDocumentNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public CaseDocumentNotFoundException() {
		super();
	}
	
	public CaseDocumentNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public CaseDocumentNotFoundException(String message) {
		super(message);
	}
	
	public CaseDocumentNotFoundException(Throwable cause) {
		super(cause);
	}
	
}