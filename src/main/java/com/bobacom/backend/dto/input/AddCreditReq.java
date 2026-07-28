package com.bobacom.backend.dto.input;

import java.math.BigDecimal;

import com.bobacom.backend.enums.Ruolo;
import com.bobacom.backend.service.implementation.UtenteImplementation;
import com.bobacom.backend.service.interfaces.IUtenteService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Classe atta a gestire l'aggiunta di credito
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AddCreditReq {

	/**
	 * Id dell'utente a cui aumentare il credito, se un utente è loggato si assume che conosca il suo id,
	 * se è  un admin si assume che sappia l'id dell'utente da aggiornare, non serve password perché sono sempre loggati
	 */
	private Integer userId;
	/**
	 * Valore del credito da aggiungere, {@link BigDecimal} perché si deve garantire una precisione esatta
	 */
	private BigDecimal credit;
	/**
	 * Valore di sicurezza per impedire chiamate a caso, deve corrispondere a {@link UtenteImplementation#creditoSecret}
	 */
	private String secret;
}
