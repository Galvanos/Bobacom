package com.bobacom.backend.dto.map;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.bobacom.backend.dto.output.ProdottoDTO;
import com.bobacom.backend.model.Prodotto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProdottoMap {
	public static List<ProdottoDTO> buildProdottoDTOList(Set<Prodotto> lP) {
		log.debug("entered buildProdottoDTOList");
	    return Optional.ofNullable(lP)
	                   .orElse(Collections.emptySet())
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
	            .tag(TagMap.buildTagProdottoDTOList(p.getTag().stream().collect(Collectors.toList())))                    
	            .promozione(PromozioneMap.buildPromozioneDTOList(p.getPromozione().stream().collect(Collectors.toList())))
	            .composizione(ComposizioneMap.buildComposizioneDTOList(p.getComposizione())) 
	            .build();
	
	}

}