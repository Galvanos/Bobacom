package com.bobacom.backend.service.implementation;

import java.util.Collections;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.bobacom.backend.dto.output.KeyValuesDTO;
import com.bobacom.backend.exceptions.AcademyException;
import com.bobacom.backend.model.KeyValues;
import com.bobacom.backend.repository.IKeyValuesRepository;
import com.bobacom.backend.service.interfaces.IKeyValuesService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KeyValuesImplementation implements IKeyValuesService {

	private final IKeyValuesRepository repository;
	
	@Transactional
	@Override
	public KeyValuesDTO create(String key, List<String> values) throws Exception {
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

	@Transactional
	@Override
	public KeyValuesDTO update(String key, List<String> values) throws Exception {
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
		List<KeyValues> found = repository.findAll();
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

}
