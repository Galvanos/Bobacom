package com.bobacom.backend.dto.map;

import com.bobacom.backend.dto.output.OrdineDTO;
import com.bobacom.backend.model.Ordine;

public class OrdineMap {
	public static OrdineDTO buildOrdineDTO(Ordine ordine) {
		return OrdineDTO.builder()
				.id(ordine.getId())
				.utente(ordine.getUtente())
				.prezzoTotale(ordine.getPrezzoTotale())
				.status(ordine.getStatus().toString())
				.dataCreazione(ordine.getDataCreazione().toString())
				.indirizzoDestinazione(ordine.getIndirizzoDestinazione())
				.build();
	}

}
