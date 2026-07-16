package com.bobacom.backend.dto.output;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode
public class KeyValuesDTO {

	/**
	 * Chiave a cui sono associati valori stringa
	 */
	private String key;
	
	/**
	 * Valori associati alla chiave
	 * <br/>
	 * <strong>Attenzione: non viene data alcuna garanzia se la lista fornita sia mutabile o meno</strong>
	 */
	private List<String> values;
	
	/**
	 * Recupera il primo valore da {@link #values}
	 * @return il primo valore di {@link #values} o null se {@link #values} é vuoto o null
	 */
	public String getValue() {
		if(values != null && !values.isEmpty()) {
			//usando find first con gli stream dava null pointer exception se il primo elemento è null
			return values.get(0);
		}else {
			return null;
		}
	}
	
	
	
}
