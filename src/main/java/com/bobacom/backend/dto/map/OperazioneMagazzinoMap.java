package com.bobacom.backend.dto.map;

import com.bobacom.backend.dto.output.OperazioneMagazzinoDTO;
import com.bobacom.backend.model.OperazioneMagazzino;
import com.bobacom.backend.utilities.DateOperations;

public class OperazioneMagazzinoMap {
	public static OperazioneMagazzinoDTO buildOperazioneMagazzinoDTO(OperazioneMagazzino operazione) {
		return OperazioneMagazzinoDTO.builder().id(operazione.getId())
												.causale(operazione.getCausale())
												.deltaQuantita(operazione.getDeltaQuantita())
												.ingrediente(IngredienteMap.buildIngredienteDTO(operazione.getIngrediente()))
												.data(DateOperations.dateToString(operazione.getData()))
												.build();
	}
}
