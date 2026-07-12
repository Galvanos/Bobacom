package com.bobacom.backend.dto.input;

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
public class OperazioneMagazzinoRequest {
	private Integer idIngrediente;	
	private Float deltaQuantita;	
	private String causale;	
	private String data;
}
