/**
 * 
 */
package com.bobacom.backend.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Eccezione da lanciare in caso di problemi di autenticazione
 * per fare si che in global exception handler restituisca {@link HttpStatus#UNAUTHORIZED}
 */
public class UnauthorizedException extends AcademyException {

	/**
	 * Costruttore senza parametri, invoca solo il super, inserito per avere esplicitamente un costruttore 
	 * senza parametri
	 */
	public UnauthorizedException() {
		super();
	}

	/**
	 * Costruttore per dotare l'eccezione di un messaggio
	 * @param message messaggio associato a questa eccezione
	 * @see #getMessage()
	 */
	public UnauthorizedException(String message) {
		super(message);
	}

	/**
	 * Costruttore per sollevare l'eccezione passando un'altra eccezione come causa
	 * @param cause causa di questa eccezione
	 * @see #getCause()
	 */
	public UnauthorizedException(Throwable cause) {
		super(cause);
	}

	/**
	 * Costruttore per fornire a questa eccezione un messaggio ed un'altra eccezione come causa
	 * @param message messaggio associato a questa eccezione
	 * @param cause causa di questa eccezione
	 * @see #getMessage()
	 * @see #getCause()
	 */
	public UnauthorizedException(String message, Throwable cause) {
		super(message, cause);
	}

}
