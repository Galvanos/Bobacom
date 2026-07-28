package com.bobacom.backend.dto.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComposizioneReq {
	private Integer idProdotto;
	private Integer idIngrediente;
	private Integer quantita;
}

