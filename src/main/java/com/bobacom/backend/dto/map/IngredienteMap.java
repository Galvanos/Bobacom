package com.bobacom.backend.dto.map;

import com.bobacom.backend.dto.output.IngredienteDTO;
import com.bobacom.backend.model.Ingrediente;

public class IngredienteMap {
	public static IngredienteDTO buildIngredienteDTO(Ingrediente ingrediente) {
		return IngredienteDTO.builder().id(ingrediente.getId())
										.nome(ingrediente.getNome())
										.colore(ingrediente.getColore())
										.descrizione(ingrediente.getDescrizione())
										.prezzoRestock(ingrediente.getPrezzoRestock())
										.sovraprezzoAggiunta(ingrediente.getSovraprezzoAggiunta())
										.quantitaStock(ingrediente.getQuantitaStock())
										.categoriaIngrediente(CategoriaIngredienteMap.buildCategoriaIngredienteDTO(
												ingrediente.getCategoriaIngrediente()))
										.allergeni(null)
										.build();
	}
}
