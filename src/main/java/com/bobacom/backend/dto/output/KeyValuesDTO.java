package com.bobacom.backend.dto.output;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Classe atta ad incapsulare la chiave con i suoi valori
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class KeyValuesDTO {

	/**
	 * Chiave a cui sono associati valori stringa
	 */
	private String key;
	
	/**
	 * Valori associati alla chiave
	 */
	private List<String> values;
	
	/**
	 * Recupera il primo valore da {@link #values}
	 * @return il primo valore di {@link #values} o null se {@link #values} é vuoto o null
	 */
	public String getValue() {
		if(values != null) {
			return values.stream().findFirst().orElse(null);
		}else {
			return null;
		}
	}
	
	
	
}
