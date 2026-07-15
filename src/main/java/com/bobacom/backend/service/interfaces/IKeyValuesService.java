package com.bobacom.backend.service.interfaces;

import java.util.List;

import com.bobacom.backend.dto.output.KeyValuesDTO;

public interface IKeyValuesService {
	
	/**
	 * Crea una chiave associandovi più valori
	 * @param key chiave
	 * @param values  valori da associare
	 * @return un oggetto contenente la chiave con i valori creati
	 * @throws Exception in caso di errori, ad esempio di chiave già esistente
	 */
	KeyValuesDTO create(String key,List<String> values) throws Exception;
	/**
	 * Crea una chiave associandovi un solo valore
	 * @param key chiave
	 * @param value  valore da associare
	 * @return un oggetto contenente la chiave con il valore creato
	 * @throws Exception in caso di errori, ad esempio di chiave già esistente
	 */
	KeyValuesDTO create(String key, String value) throws Exception;
	
	/**
	 * Sostituisce i valori associati ad una chiave
	 * @param key chiave
	 * @param values  valori da associare
	 * @return un oggetto contenente la chiave con i valori aggiornati
	 * @throws Exception in caso di errori, ad esempio di chiave non esistente
	 */
	KeyValuesDTO update(String key,List<String> values) throws Exception;
	/**
	 * Sostituisce i valori associati ad una chiave, associandovi un solo valore
	 * @param key chiave
	 * @param value  valore da associare
	 * @return un oggetto contenente la chiave con il valore creato
	 * @throws Exception in caso di errori, ad esempio di chiave non esistente
	 */
	KeyValuesDTO update(String key, String value) throws Exception;
	
	/**
	 * Fornisce tutte le chiavi e i valori ad esse associati
	 * @return una {@link List} di {@link KeyValuesDTO} che incapsula le chiavi con i valori
	 * @throws Exception in caso di errori
	 */
	List<KeyValuesDTO>  list() throws Exception;
	
	/**
	 * Recupera la chiave con i suoi valori
	 * @param key chiave da cercare
	 * @return un oggetto {@link KeyValuesDTO} che incapsula la chiave con i suoi valori
	 * @throws Exception in caso di errori, ad esempio di chiave inesistente
	 */
	KeyValuesDTO read(String key) throws Exception;
	
	/**
	 * Cancella la chiave ed i valori associati
	 * @param key la chiave da cancellare
	 * @return un oggetto {@link KeyValuesDTO} che incapsula la chiave appen cancellata con i suoi valori
	 * @throws Exception in caso di errori, ad esempio di chiave non esistente
	 */
	KeyValuesDTO delete(String key) throws Exception;
	
	/**
	 * Verifica se un chiave è già memorizzata
	 * @param key la chiave da verificare
	 * @return true se già memorizzata, false altrimenti
	 * @throws Exception in caso di errori
	 */
	boolean existsKey(String key) throws Exception;

}
