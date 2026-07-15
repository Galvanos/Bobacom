package com.bobacom.backend.dto.map;

import com.bobacom.backend.dto.output.AllergeniDTO;
import com.bobacom.backend.model.Allergeni;

public class AllergeniMap {
	public static AllergeniDTO buildAllergeniDTO(Allergeni allergeni) {
		return AllergeniDTO.builder().id(allergeni.getId()).nome(allergeni.getNome()).urlIcona(allergeni.getUrlIcona()).build();
	}
}
