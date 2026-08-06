package com.bobacom.backend.exceptions;

/**
 * Eccezione da lanciare se non si trova l'utente, 
 * da intercettare per poi rilanciare un'eccezione più adeguata
 */
public class UserNotFoundException extends AcademyException {

	/**
	 * Costruttore senza parametri, invoca solo il super, inserito per avere esplicitamente un costruttore 
	 * senza parametri
	 */
	public UserNotFoundException() {
		super();
	}

	/**
	 * Costruttore per dotare l'eccezione di un messaggio
	 * @param message messaggio associato a questa eccezione
	 * @see #getMessage()
	 */
	public UserNotFoundException(String message) {
		super(message);
	}

	/**
	 * Costruttore per sollevare l'eccezione passando un'altra eccezione come causa
	 * @param cause causa di questa eccezione
	 * @see #getCause()
	 */
	public UserNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * Costruttore per fornire a questa eccezione un messaggio ed un'altra eccezione come causa
	 * @param message messaggio associato a questa eccezione
	 * @param cause causa di questa eccezione
	 * @see #getMessage()
	 * @see #getCause()
	 */
	public UserNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
