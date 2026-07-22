package com.bobacom.backend.dto.map;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.bobacom.backend.dto.output.PromozioneDTO;
import com.bobacom.backend.model.Promozione;

public class PromozioneMap {
	public static List <PromozioneDTO> buildPromozioneDTOList(List <Promozione> lP) {
	    return Optional.ofNullable(lP)
	                   .orElse(Collections.emptyList())
	                   .stream()
	                   .map(s -> buildPromozioneDTO(s))
	                   .collect(Collectors.toList()); 
	}
	
	public static PromozioneDTO buildPromozioneDTO(Promozione p) {
	    return PromozioneDTO.builder()
	            .id(p.getId())
	            .sconto(p.getSconto())
	            .isActive(p.getIsActive())
	            .prodotto(ProdottoMap.buildProdottoDTOList(p.getProdotto())) 
	            .build();
	}

}