package com.bobacom.backend.dto.input;

import java.math.BigDecimal;

import com.bobacom.backend.dto.input.validation.ValidationGroups;

import jakarta.validation.constraints.NotNull;
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
	@NotNull(groups = ValidationGroups.Create.class, message = "id ingrediente non fornito")
	private Integer idIngrediente;	
	@NotNull(groups = ValidationGroups.Create.class, message = "delta quantitá non fornito")
	private BigDecimal deltaQuantita;
	private String causale;
	@NotNull(groups = ValidationGroups.Create.class, message = "data non fornita")
	private String data;
}
