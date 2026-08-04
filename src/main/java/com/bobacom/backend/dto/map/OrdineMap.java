package com.bobacom.backend.dto.map;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import com.bobacom.backend.dto.output.OrdineDTO;
import com.bobacom.backend.model.Ordine;
import com.bobacom.backend.utilities.DateOperations;

public class OrdineMap {
	public static List<OrdineDTO> buildOrdineDTOList(List<Ordine> orderList) {
	    return Optional.ofNullable(orderList)
	                   .orElse(Collections.emptyList())
	                   .stream()
	                   .map(s -> buildOrdineDTO(s))
	                   .collect(Collectors.toList()); 
	}
	
	public static OrdineDTO buildOrdineDTO(Ordine o) {
	    return OrdineDTO.builder()
	            .id(o.getId())
	            .dataCreazione(DateOperations.dateToString(o.getDataCreazione()))
	            .indirizzoDestinazione(o.getIndirizzoDestinazione())
	            .prezzoTotale(o.getPrezzoTotale())
	            .utente(UtenteMap.buildUtenteDTO(o.getUtente(), true))
	            .status(o.getStatus().toString())
	            .ordineProdotto(OrdineProdottoMap.buildOrdineProdottoDTOList(o.getOrdineProdotti().stream().toList()))
	            .build();
	
	}
}
