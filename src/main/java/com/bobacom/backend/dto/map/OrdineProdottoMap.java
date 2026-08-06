package com.bobacom.backend.dto.map;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.bobacom.backend.dto.output.OrdineProdottoDTO;
import com.bobacom.backend.model.OrdineProdotto;

public class OrdineProdottoMap {
	public static List<OrdineProdottoDTO> buildOrdineProdottoDTOList(List<OrdineProdotto> orderPList) {
	    return Optional.ofNullable(orderPList)
	                   .orElse(Collections.emptyList())
	                   .stream()
	                   .map(s -> buildOrdineProdottoDTO(s))
	                   .collect(Collectors.toList()); 
	}
	
	public static OrdineProdottoDTO buildOrdineProdottoDTO(OrdineProdotto op) {
	    return OrdineProdottoDTO.builder()
	            .id(op.getId())
	            .ordineId(op.getOrdine().getId())
	            .summary(op.getSummary())
	            .quantita(op.getQuantita())
	            .prezzo(op.getPrezzo())
	            .prodotto(ProdottoMap.buildProdottoDTO(op.getProdotto()))
	            .build();
	
	}
}
