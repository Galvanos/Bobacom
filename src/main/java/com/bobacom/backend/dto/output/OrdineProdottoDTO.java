package com.bobacom.backend.dto.output;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrdineProdottoDTO {
	private Integer id;
	private Integer ordineId;
	private ProdottoDTO prodotto;
	private String summary;
	private Integer quantita;
	private BigDecimal prezzo;
}
