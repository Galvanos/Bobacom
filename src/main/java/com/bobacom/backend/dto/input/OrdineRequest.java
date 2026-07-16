package com.bobacom.backend.dto.input;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.bobacom.backend.enums.StatoSpedizione;
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
	private Utente utente;
	private BigDecimal prezzoTotale;
	private StatoSpedizione status;
	private LocalDate dataCreazione;
	private String indirizzoDestinazione;
}
