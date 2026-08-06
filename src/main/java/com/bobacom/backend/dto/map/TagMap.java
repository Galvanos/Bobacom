package com.bobacom.backend.dto.map;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.bobacom.backend.dto.output.TagProdottoDTO;
import com.bobacom.backend.model.TagProdotto;

public class TagMap {
	public static List <TagProdottoDTO> buildTagProdottoDTOList(List <TagProdotto> lT) {
	    return Optional.ofNullable(lT)
	                   .orElse(Collections.emptyList())
	                   .stream()
	                   .map(s -> buildTagProdottoDTO(s))
	                   .collect(Collectors.toList()); 
	}
	
	public static TagProdottoDTO buildTagProdottoDTO(TagProdotto t) {
		return TagProdottoDTO.builder()
				.id(t.getId())
				.nome(t.getNome())
				.descrizione(t.getDescrizione())
				.build();
	}

}