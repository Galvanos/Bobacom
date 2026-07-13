package com.bobacom.backend.mapping;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.bobacom.backend.dto.output.ProdottoDTO;
import com.bobacom.backend.model.Prodotto;

public class ProdottoMap {
	public static List<ProdottoDTO> buildProdottoDTOList(List<Prodotto> lP) {
	    return Optional.ofNullable(lP)
	                   .orElse(Collections.emptyList())
	                   .stream()
	                   .map(s -> buildProdottoDTO(s))
	                   .collect(Collectors.toList()); 
	}
	
	public static ProdottoDTO buildProdottoDTO(Prodotto p) {
		return ProdottoDTO.builder()
				.id(p.getId())
				.nome(p.getNome())
				.descrizione(p.getDescrizione())
				.imgUrl(p.getImgUrl())
				.tag(p.getTag())
				.promozione(p.getPromozione())
				.composizione(p.getComposizione())
				.build();
	}

}
