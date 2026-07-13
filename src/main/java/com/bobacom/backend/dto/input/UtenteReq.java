package com.bobacom.backend.dto.input;

import java.math.BigDecimal;

import com.bobacom.backend.enums.Ruolo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UtenteReq {

	//TODO aggiungere validazioni
	private Integer id;
	private String username;
	private String email;
	private String password;
	private Ruolo ruolo;
	private BigDecimal credito;
	private String indirizzo;
}
