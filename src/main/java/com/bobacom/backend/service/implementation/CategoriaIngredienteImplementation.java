package com.bobacom.backend.service.implementation;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bobacom.backend.dto.input.CategoriaIngredienteRequest;
import com.bobacom.backend.dto.output.CategoriaIngredienteDTO;
import com.bobacom.backend.exceptions.AcademyException;
import com.bobacom.backend.model.CategoriaIngrediente;
import com.bobacom.backend.repository.ICategoriaRepository;
import com.bobacom.backend.service.interfaces.ICategoriaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaIngredienteImplementation implements ICategoriaService{
	private final ICategoriaRepository catRepo;

	@Override
	public void create(CategoriaIngredienteRequest req) throws Exception {
		CategoriaIngrediente categoria = CategoriaIngrediente.builder().nome(req.getNome()).build();
		catRepo.save(categoria);
	}

	@Override
	public void update(CategoriaIngredienteRequest req) throws Exception {
		CategoriaIngrediente categoria = catRepo.findById(req.getId()).orElseThrow(
				() -> new AcademyException("no such categoriaIngrediente"));
		Optional.ofNullable(req.getNome()).ifPresent(categoria::setNome);
		catRepo.save(categoria);
	}

	@Override
	public void delete(Integer id) throws Exception {
		CategoriaIngrediente categoria = catRepo.findById(id).orElseThrow(
				() -> new AcademyException("no such categoriaIngrediente"));
		catRepo.delete(categoria);
	}

	@Override
	public List<CategoriaIngredienteDTO> list() throws Exception {
		List<CategoriaIngrediente> catList = catRepo.findAll();
		return catList.stream().map(c -> CategoriaIngredienteDTO.builder().id(c.getId()).nome(c.getNome()).build()).toList();
	}

	@Override
	public CategoriaIngredienteDTO getById(Integer id) throws Exception {
		return CategoriaIngredienteDTO.builder().id(id).nome(catRepo.findById(id).orElseThrow(
				() -> new AcademyException("no such categoriaIngrediente")).getNome()).build();
	}

}
