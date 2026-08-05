package com.bobacom.backend.dto.input;

import com.bobacom.backend.dto.input.validation.ValidationGroups;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComposizioneReq {
	private Integer idProdotto;
	@NotNull(groups = ValidationGroups.Create.class, message = "id ingrediente non fornito")
	private Integer idIngrediente;
	@NotNull(groups = ValidationGroups.Create.class, message = "quantitá non fornita")
	private Integer quantita;
}

