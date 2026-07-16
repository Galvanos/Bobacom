package com.bobacom.backend.dto.input;

import java.math.BigDecimal;
import java.util.List;

import com.bobacom.backend.dto.input.validation.ValidationGroups;

import jakarta.validation.constraints.NotNull;
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
	@NotNull(groups = ValidationGroups.Update.class, message = "id ingrediente non fornito")
	private Integer id;
	@NotNull(groups = ValidationGroups.Create.class, message = "id ingrediente non fornito")
	private String nome;
	@NotNull(groups = ValidationGroups.Create.class, message = "descrizione non fornita")
	private String descrizione;
	private BigDecimal quantitaStock;
	@NotNull(groups = ValidationGroups.Create.class, message = "sovraprezzo aggiunta non fornito")
	private Float sovraprezzoAggiunta;
	@NotNull(groups = ValidationGroups.Create.class, message = "prezzo restock non fornito")
	private Float prezzoRestock;
	@NotNull(groups = ValidationGroups.Create.class, message = "colore non fornito")
	private String colore;
	private List<Integer> idAllergene;
	@NotNull(groups = ValidationGroups.Create.class, message = "categoria ingrediente non fornita")
	private Integer idCategoriaIngrediente;
}
