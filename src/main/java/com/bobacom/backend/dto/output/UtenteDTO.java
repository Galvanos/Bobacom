package com.bobacom.backend.dto.output;

import java.math.BigDecimal;
import java.util.List;

import com.bobacom.backend.enums.Ruolo;
import com.bobacom.backend.model.Composizione;
import com.bobacom.backend.model.Promozione;
import com.bobacom.backend.model.TagProdotto;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class UtenteDTO {

	//TODO aggiungere validazioni
	private Integer id;
	private String username;
	private String email;
	private String password;
	private Ruolo ruolo;
	private BigDecimal credito;
	private String indirizzo;
}
