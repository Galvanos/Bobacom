package com.bobacom.backend.dto.output;

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
	private Float deltaQuantita;
	private String causale;
	private String data;
}
