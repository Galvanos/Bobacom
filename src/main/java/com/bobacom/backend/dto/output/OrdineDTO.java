package com.bobacom.backend.dto.output;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrdineDTO {
	    private Integer id;
		private UtenteDTO utente;
		private BigDecimal prezzoTotale;
		private String status;
		private String dataCreazione;
		private String indirizzoDestinazione;
		private List<OrdineProdottoDTO> ordineProdotto;
}
