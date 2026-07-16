package com.bobacom.backend.dto.map;

import com.bobacom.backend.dto.output.CategoriaIngredienteDTO;
import com.bobacom.backend.model.CategoriaIngrediente;

public class CategoriaIngredienteMap {
	public static CategoriaIngredienteDTO buildCategoriaIngredienteDTO(CategoriaIngrediente categoriaIngrediente) {
		return CategoriaIngredienteDTO.builder().id(categoriaIngrediente.getId()).nome(categoriaIngrediente.getNome()).build();
	}
}
