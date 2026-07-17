package com.bobacom.backend.service.implementation;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bobacom.backend.dto.input.IngredienteRequest;
import com.bobacom.backend.dto.map.IngredienteMap;
import com.bobacom.backend.dto.output.IngredienteDTO;
import com.bobacom.backend.exceptions.AcademyException;
import com.bobacom.backend.model.CategoriaIngrediente;
import com.bobacom.backend.model.Ingrediente;
import com.bobacom.backend.repository.IAllergeniRepository;
import com.bobacom.backend.repository.ICategoriaRepository;
import com.bobacom.backend.repository.IIngredienteRepository;
import com.bobacom.backend.service.interfaces.IIngredienteService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class IngredienteImplementation implements IIngredienteService{
	private final IIngredienteRepository ingredienteRepo;
	private final ICategoriaRepository categoriaRepo;
	private final IAllergeniRepository allergeniRepo;

	@Transactional
	@Override
	public void create(IngredienteRequest req) throws Exception{
		CategoriaIngrediente categoria = categoriaRepo.findById(req.getIdCategoriaIngrediente())
				.orElseThrow(() -> new Exception("no such categoria with id: " + req.getIdCategoriaIngrediente()));
		
		Ingrediente ingrediente = Ingrediente.builder().nome(req.getNome())
														.descrizione(req.getDescrizione())
														.colore(req.getColore())
														.quantitaStock(Optional.ofNullable(
																req.getQuantitaStock()).orElse(BigDecimal.ZERO))
														.sovraprezzoAggiunta(req.getSovraprezzoAggiunta())
														.prezzoRestock(req.getPrezzoRestock())
														.categoriaIngrediente(categoria)
														.build();
		for(Integer id : req.getIdAllergene())
			ingrediente.addAllergene(allergeniRepo.findById(id)
					.orElseThrow(() -> new Exception("no such allergene with id: " + id)));
		Integer insertedId = ingredienteRepo.save(ingrediente).getId();
		log.debug("inserted ingredient id: {}", insertedId);
	}
	@Transactional
	@Override
	public void update(IngredienteRequest req) throws Exception{
		Ingrediente ingrediente = ingredienteRepo.findById(req.getId()).orElseThrow(
				() -> new AcademyException("ingrediente non trovato;"));
		Optional.ofNullable(req.getIdCategoriaIngrediente())
			.ifPresent(id -> categoriaRepo.findById(id)
		        .ifPresentOrElse(ingrediente::setCategoriaIngrediente, () -> {
		        	throw new AcademyException("no such categoria"); }));
		Optional.ofNullable(req.getIdAllergene()).stream().flatMap(Collection::stream)
			.forEach(a -> allergeniRepo.findById(a).ifPresentOrElse(ingrediente::addAllergene, () -> {
	        	throw new AcademyException("no such categoria"); }));
		Optional.ofNullable(req.getColore()).ifPresent(ingrediente::setColore);
		Optional.ofNullable(req.getDescrizione()).ifPresent(ingrediente::setDescrizione);
		Optional.ofNullable(req.getNome()).ifPresent(ingrediente::setNome);
		Optional.ofNullable(req.getQuantitaStock()).ifPresent(ingrediente::setQuantitaStock);
		Optional.ofNullable(req.getSovraprezzoAggiunta()).ifPresent(ingrediente::setSovraprezzoAggiunta);
		Optional.ofNullable(req.getPrezzoRestock()).ifPresent(ingrediente::setPrezzoRestock);
		ingredienteRepo.save(ingrediente);
	}
	@Transactional
	@Override
	public void delete(Integer id) throws Exception{
		Ingrediente ing = ingredienteRepo.findById(id).orElseThrow(() -> new AcademyException("ingrediente non trovato"));
		ingredienteRepo.delete(ing);		
	}
	@Override
	public List<IngredienteDTO> list() throws Exception {
		return ingredienteRepo.findAll().stream().map(i -> IngredienteMap.buildIngredienteDTO(i)).collect(Collectors.toList());
	}
	@Override
	public IngredienteDTO getById(Integer id) throws Exception {
		return  IngredienteMap.buildIngredienteDTO(ingredienteRepo.findById(id).orElseThrow(
				() -> new AcademyException()));
	}
	
	
	
}
