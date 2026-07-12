package com.bobacom.backend.service.implementation;

import org.springframework.stereotype.Service;

import com.bobacom.backend.dto.input.OperazioneMagazzinoRequest;
import com.bobacom.backend.exceptions.AcademyException;
import com.bobacom.backend.model.Ingrediente;
import com.bobacom.backend.model.OperazioneMagazzino;
import com.bobacom.backend.repository.IIngredienteRepository;
import com.bobacom.backend.repository.IOperazioneMagazzinoRepository;
import com.bobacom.backend.service.interfaces.IOperazioneMagazzinoService;
import com.bobacom.backend.utilities.DateOperations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class OperazioneMagazzinoImplementation implements IOperazioneMagazzinoService{
	private final IOperazioneMagazzinoRepository operazioneRepo;
	private final IIngredienteRepository ingredienteRepo;
	
	@Override
	public void create(OperazioneMagazzinoRequest req) {
		Ingrediente ingrediente = ingredienteRepo.findById(req.getIdIngrediente())
									.orElseThrow(() -> new AcademyException("no such ingrediente"));
		
		OperazioneMagazzino operazione = OperazioneMagazzino.builder().deltaQuantita(req.getDeltaQuantita())
																		.data(DateOperations.stringToDate(req.getData()))
																		.causale(req.getCausale())
																		.ingrediente(ingrediente)
																		.build();
		Integer insertedId = operazioneRepo.save(operazione).getId();
		log.debug("id operazione inserita: {}", insertedId);
	}

}
