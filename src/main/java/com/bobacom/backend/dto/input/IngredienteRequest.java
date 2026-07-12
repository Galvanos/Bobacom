package com.bobacom.backend.dto.input;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredienteRequest {
	private Integer id;
	private String nome;
	private String descrizione;
	private Long quantitaStock;
	private float sovraprezzoAggiunta;
	private float prezzoRestock;
	private String colore;
	private List<Integer> idAllergene;
	private Integer idCategoriaIngrediente;
}
