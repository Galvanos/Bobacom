package com.bobacom.backend.dto.input;

import java.util.List;

import com.bobacom.backend.model.Ingrediente;
import com.bobacom.backend.model.Prodotto;
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
public class ComposizioneReq {
	private Integer id;
	private Prodotto prodotto;
	private Ingrediente ingrediente;
	private Integer quantita;
}

