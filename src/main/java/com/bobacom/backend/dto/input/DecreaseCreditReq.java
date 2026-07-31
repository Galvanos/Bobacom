package com.bobacom.backend.dto.input;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
/**
 * Classe atta a gestire le richieste di diminuzione del credito
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DecreaseCreditReq {

	/**
	 * Id dell'utente a cui diminuire il credito,
	 * nelle chiamate da parte dell'utente può essere omesso 
	 */
	private Integer userId;
	
	/**
	 * Valore del credito da diminuire
	 */
	private BigDecimal credit;

}
