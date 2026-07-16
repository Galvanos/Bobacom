package com.bobacom.backend.dto.output;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoriaIngredienteDTO {
	private Integer id;
	private String nome;
}