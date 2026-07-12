package com.bobacom.backend.service.implementation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bobacom.backend.dto.input.IngredienteRequest;
import com.bobacom.backend.model.Allergeni;
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
		List<Allergeni> allergeni = new ArrayList<Allergeni>();
		for(Integer id : req.getIdAllergene())
			allergeni.add(allergeniRepo.findById(id).orElseThrow(() -> new Exception("no such allergene with id: " + id)));
		Ingrediente ingrediente = Ingrediente.builder().nome(req.getNome())
														.descrizione(req.getDescrizione())
														.colore(req.getColore())
														.quantitaStock(req.getQuantitaStock())
														.sovraprezzoAggiunta(req.getSovraprezzoAggiunta())
														.prezzoRestock(req.getPrezzoRestock())
														.categoriaIngrediente(categoria)
														.allergeni(allergeni)
														.build();
		Integer insertedId = ingredienteRepo.save(ingrediente).getId();
		log.debug("inserted ingredient id: {}", insertedId);
	}
	@Transactional
	@Override
	public void update(IngredienteRequest req) {
		// TODO Auto-generated method stub
		
	}
	@Transactional
	@Override
	public void delete(Integer id) {
		// TODO Auto-generated method stub
		
	}
}
