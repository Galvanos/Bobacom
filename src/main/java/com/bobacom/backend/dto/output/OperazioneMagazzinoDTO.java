package com.bobacom.backend.dto.output;

import java.math.BigDecimal;

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
public class OperazioneMagazzinoDTO {
	private Integer id;
	private IngredienteDTO ingrediente;
	private BigDecimal deltaQuantita;
	private String causale;
	private String data;
}
