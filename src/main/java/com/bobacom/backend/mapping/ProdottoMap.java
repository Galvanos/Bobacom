package com.bobacom.backend.mapping;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.bobacom.backend.dto.output.ComposizioneDTO;
import com.bobacom.backend.dto.output.ProdottoDTO;
import com.bobacom.backend.dto.output.PromozioneDTO;
import com.bobacom.backend.dto.output.TagProdottoDTO;
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
	            .tag(TagMap.buildTagProdottoDTOList(p.getTag()))                    
	            .promozione(PromozioneMap.buildPromozioneDTOList(p.getPromozione()))
	            .composizione(ComposizioneMap.buildComposizioneDTOList(p.getComposizione())) 
	            .build();
	
	}

}
