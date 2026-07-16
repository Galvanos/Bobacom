package com.bobacom.backend.dto.input;

import java.math.BigDecimal;

import com.bobacom.backend.model.Utente;

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
public class OrdineRequest {
    private Integer id;
	private Integer utente_id;
	private BigDecimal prezzoTotale;
	private String status;
	private String dataCreazione;
	private String indirizzoDestinazione;
}
