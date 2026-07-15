package com.bobacom.backend.service.implementation;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bobacom.backend.dto.output.KeyValuesDTO;
import com.bobacom.backend.exceptions.AcademyException;
import com.bobacom.backend.model.KeyValues;
import com.bobacom.backend.repository.IKeyValuesRepository;
import com.bobacom.backend.service.interfaces.IKeyValuesService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeyValuesImplementation implements IKeyValuesService {

	private final IKeyValuesRepository repository;
	
	@Override
	public KeyValuesDTO create(String key, List<String> values) throws Exception {
		return createInner(key, values);
	}

	/**
	 * Implementazione del create per poter essere invocato anche se si passa un null
	 * @param key chiave scelta
	 * @param values valori da impostare
	 * @return un oggetto contenente la chiave con i valori creati
	 * @throws Exception in caso di errori
	 */
	@Transactional
	private KeyValuesDTO createInner(String key, List<String> values) throws Exception {
		if(key == null) {
			throw new AcademyException("La chiave non può essere null");
		}
		boolean existsById = repository.existsById(key);
		if(existsById) {
			throw new AcademyException("Chiave "+key+" già esistente");
		}
		KeyValues toInsert = new KeyValues(key, values);
		toInsert = repository.save(toInsert);
		return KeyValuesDTO.builder()
				.key(toInsert.getKey())
				.values(toInsert.getValues())
				.build();
	}

	@Override
	public KeyValuesDTO create(String key, String value) throws Exception {
		return create(key, Collections.singletonList(value));
	}
	
	

	
	@Override
	public KeyValuesDTO update(String key, List<String> values) throws Exception {
		return updateInner(key, values);
	}

	
	/**
	 * Implementazione dell'update per poter essere invocato anche se si passa un null
	 * @param key chiave scelta
	 * @param values valori da impostare
	 * @return un oggetto contenente la chiave con i valori creati
	 * @throws Exception in caso di errori
	 */
	@Transactional
	private KeyValuesDTO updateInner(String key, List<String> values) {
		if(key == null) {
			throw new AcademyException("La chiave non può essere null");
		}
		KeyValues toUpdate = repository.findById(key).orElseThrow(() -> new AcademyException("Chiave "+key+" non trovata"));
		toUpdate.setValues(values);
		toUpdate = repository.save(toUpdate);
		return KeyValuesDTO.builder()
				.key(toUpdate.getKey())
				.values(toUpdate.getValues())
				.build();
	}

	@Override
	public KeyValuesDTO update(String key, String value) throws Exception {
		return update(key, Collections.singletonList(value));
	}

	@Override
	public List<KeyValuesDTO> list() throws Exception {
		List<KeyValues> found = repository.findAll(Sort.by("key").ascending());//meglio dare un ordinamento per i test e per avere un risultato più prevedibile
		return found.stream().map(kv -> 
		  KeyValuesDTO.builder()
				.key(kv.getKey())
				.values(kv.getValues())
				.build()).toList();
	}

	@Override
	public KeyValuesDTO read(String key) throws Exception {
		KeyValues found = repository.findById(key).orElseThrow(() -> new AcademyException("Chiave "+key+" non trovata"));
		return KeyValuesDTO.builder()
				.key(found.getKey())
				.values(found.getValues())
				.build();
	}

	@Transactional
	@Override
	public KeyValuesDTO delete(String key) throws Exception {
		KeyValues found = repository.findById(key).orElseThrow(() -> new AcademyException("Chiave "+key+" non trovata"));
		repository.delete(found);
		return KeyValuesDTO.builder()
				.key(found.getKey())
				.values(found.getValues())
				.build();
	}

	@Override
	public boolean existsKey(String key) throws Exception {
		return repository.existsById(key);
	}

	@Override
	public KeyValuesDTO createNullList(String key) throws Exception {
		return createInner(key, null);
	}

	@Override
	public KeyValuesDTO createNullString(String key) throws Exception {
		return create(key,Collections.singletonList(null));
	}

	@Override
	public KeyValuesDTO updateNullList(String key) throws Exception {
		return updateInner(key, null);
	}

	@Override
	public KeyValuesDTO updateNullString(String key) throws Exception {
		return update(key, Collections.singletonList(null));
	}

}
