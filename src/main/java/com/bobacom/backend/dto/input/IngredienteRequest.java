package com.bobacom.backend.dto.input;

import java.math.BigDecimal;
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
	private BigDecimal quantitaStock;
	private Float sovraprezzoAggiunta;
	private Float prezzoRestock;
	private String colore;
	private List<Integer> idAllergene;
	private Integer idCategoriaIngrediente;
}
