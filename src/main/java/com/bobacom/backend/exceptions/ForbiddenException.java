package com.bobacom.backend.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Eccezione da lanciare in caso di problemi di autenticazione
 * per fare si che in global exception handler restituisca {@link HttpStatus#FORBIDDEN}
 */
public class ForbiddenException extends AcademyException {

	/**
	 * Costruttore senza parametri, invoca solo il super, inserito per avere esplicitamente un costruttore 
	 * senza parametri
	 */
	public ForbiddenException() {
		super();
	}
	
	/**
	 * Costruttore per fornire a questa eccezione un messaggio ed un'altra eccezione come causa
	 * @param message messaggio associato a questa eccezione
	 * @param cause causa di questa eccezione
	 * @see #getMessage()
	 * @see #getCause()
	 */
	public ForbiddenException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Costruttore per dotare l'eccezione di un messaggio
	 * @param message messaggio associato a questa eccezione
	 * @see #getMessage()
	 */
	public ForbiddenException(String message) {
		super(message);
	}

	/**
	 * Costruttore per sollevare l'eccezione passando un'altra eccezione come causa
	 * @param cause causa di questa eccezione
	 * @see #getCause()
	 */
	public ForbiddenException(Throwable cause) {
		super(cause);
	}

}
