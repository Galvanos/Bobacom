package com.bobacom.backend.dto.output;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IngredienteDTO {
	private Integer id;
	private String nome;
	private String descrizione;
	private BigDecimal quantitaStock;
	private float sovraprezzoAggiunta;
	private float prezzoRestock;
	private String colore;
	private List<AllergeniDTO> allergeni;
	private CategoriaIngredienteDTO categoriaIngrediente;
}
