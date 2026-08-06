package com.bobacom.backend.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bobacom.backend.dto.input.TagRequest;
import com.bobacom.backend.dto.map.TagMap;
import com.bobacom.backend.dto.output.TagProdottoDTO;
import com.bobacom.backend.exceptions.AcademyException;
import com.bobacom.backend.model.TagProdotto;
import com.bobacom.backend.repository.ITagProdottoRepository;
import com.bobacom.backend.service.interfaces.ITagProdottoService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TagProdottoImplementation implements ITagProdottoService{
	private final ITagProdottoRepository tagRepo;
	
	@Transactional
	@Override
	public void create(TagRequest req) throws Exception {
		TagProdotto tag = TagProdotto.builder().nome(req.getNome()).descrizione(req.getDescrizione()).build();
		tagRepo.save(tag);
	}
	@Transactional
	@Override
	public void delete(Integer id) throws Exception {
		TagProdotto tag = tagRepo.findById(id).orElseThrow(() -> new AcademyException("no such tag"));
		tagRepo.delete(tag);		
	}

	@Override
	public List<TagProdottoDTO> list() throws Exception {
		return tagRepo.findAll().stream().map(a -> TagMap.buildTagProdottoDTO(a)).toList();
	}

	@Override
	public TagProdottoDTO getById(Integer id) throws Exception {
		return TagMap.buildTagProdottoDTO(tagRepo.findById(id).orElseThrow(
				() -> new AcademyException("no such tag")));
	}
	@Override
	public List<TagProdottoDTO> listOrdered() throws Exception {
		return tagRepo.findAllByOrderById().stream().map(a -> TagMap.buildTagProdottoDTO(a)).toList();
	}
}
