package com.bobacom.backend.dto.output;

import java.util.List;

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
public class PromozioneDTO {
	private Integer id;
	private Float sconto;
	private Boolean isActive;
	List <ProdottoDTO> prodotto;

}
