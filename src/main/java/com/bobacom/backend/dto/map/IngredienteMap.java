package com.bobacom.backend.dto.map;

import java.util.stream.Collectors;

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
										.allergeni(	ingrediente.getAllergeni().stream().map(
												a -> AllergeniMap.buildAllergeniDTO(a)).collect(Collectors.toList()))
										.build();
	}
}
