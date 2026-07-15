package com.bobacom.backend.service.interfaces;

import java.util.List;

import org.springframework.stereotype.Service;

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
	 * Crea una chiave associandovi come valore una lista a null, 
	 * risolve l'ambiguità tra {@link #create(String, List)} e 
	 * {@link #create(String, String)} se si vuole passare un null, 
	 * si comporta come se si passasse null a {@link #create(String, List)}
	 * @param  la chiave da associare a una lista null di valori
	 * @return un oggetto contenente la chiave con la lista di valori a null
	 * @throws Exception  in caso di errori
	 */
	KeyValuesDTO createNullList(String key) throws Exception;
	
	
	/**
	 * Crea una chiave associandovi come valore una lista a null, 
	 * risolve l'ambiguità tra {@link #create(String, List)} e 
	 * {@link #create(String, String)} se si vuole passare un null, 
	 * si comporta come se si passasse null a {@link #create(String, String)}
	 * @param  la chiave da associare a un valore null
	 * @return un oggetto contenente la chiave con la lista contenente un solo valore a null
	 * @throws Exception  in caso di errori
	 */
	KeyValuesDTO createNullString(String key) throws Exception;
	
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
	 * Aggiorna una chiave associandovi come valore una lista a null, 
	 * risolve l'ambiguità tra {@link #update(String, List)} e 
	 * {@link #update(String, String)} se si vuole passare un null, 
	 * si comporta come se si passasse null a {@link #update(String, List)}
	 * @param  la chiave da associare a una lista null di valori
	 * @return un oggetto contenente la chiave con la lista di valori a null
	 * @throws Exception  in caso di errori
	 */
	KeyValuesDTO updateNullList(String key) throws Exception;
	
	
	/**
	 * Aggiorna una chiave associandovi come valore una lista a null, 
	 * risolve l'ambiguità tra {@link #update(String, List)} e 
	 * {@link #update(String, String)} se si vuole passare un null, 
	 * si comporta come se si passasse null a {@link #update(String, String)}
	 * @param  la chiave da associare a un valore null
	 * @return un oggetto contenente la chiave con la lista contenente un solo valore a null
	 * @throws Exception  in caso di errori
	 */
	KeyValuesDTO updateNullString(String key) throws Exception;
	
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
