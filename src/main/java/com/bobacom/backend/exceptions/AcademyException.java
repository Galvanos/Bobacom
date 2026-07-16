package com.bobacom.backend.exceptions;
/**
 * Classe per eccezione generica
 */
public class AcademyException extends RuntimeException {

	/**
	 * Costruttore senza parametri, invoca solo il super, inserito per avere esplicitamente un costruttore 
	 * senza parametri
	 */
	public AcademyException() {
		super();
	}

	/**
	 * Costruttore per dotare l'eccezione di un messaggio
	 * @param message messaggio associato a questa eccezione
	 * @see #getMessage()
	 */
	public AcademyException(String message) {
		super(message);
	}

	/**
	 * Costruttore per sollevare l'eccezione passando un'altra eccezione come causa
	 * @param cause causa di questa eccezione
	 * @see #getCause()
	 */
	public AcademyException(Throwable cause) {
		super(cause);
	}

	/**
	 * Costruttore per fornire a questa eccezione un messaggio ed un'altra eccezione come causa
	 * @param message messaggio associato a questa eccezione
	 * @param cause causa di questa eccezione
	 * @see #getMessage()
	 * @see #getCause()
	 */
	public AcademyException(String message, Throwable cause) {
		super(message, cause);
	}

	

}