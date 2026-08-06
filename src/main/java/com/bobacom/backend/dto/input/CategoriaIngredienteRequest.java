package com.bobacom.backend.dto.input;

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
public class CategoriaIngredienteRequest {
	@NotNull(groups = ValidationGroups.Update.class, message = "id categoria non fornito")
	private Integer id;
	@NotNull(groups = ValidationGroups.Create.class, message = "nome categoria non fornito")
	private String nome;
}
