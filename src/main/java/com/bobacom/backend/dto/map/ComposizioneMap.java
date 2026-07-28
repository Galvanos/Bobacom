package com.bobacom.backend.dto.map;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.bobacom.backend.dto.output.ComposizioneDTO;
import com.bobacom.backend.model.Composizione;

public class ComposizioneMap {
	public static List <ComposizioneDTO> buildComposizioneDTOList(List <Composizione> lC) {
	    return Optional.ofNullable(lC)
	                   .orElse(Collections.emptyList())
	                   .stream()
	                   .map(s -> buildComposizioneDTO(s))
	                   .collect(Collectors.toList()); 
	}
	
	public static ComposizioneDTO buildComposizioneDTO(Composizione c) {
	    return ComposizioneDTO.builder()
	            .id(c.getId())
	            .prodotto(ProdottoMap.buildProdottoDTO(c.getProdotto())) 
	            .ingrediente(IngredienteMap.buildIngredienteDTO(c.getIngrediente())) 
	            .quantita(c.getQuantita())
	            .build();
	}
	
	
}