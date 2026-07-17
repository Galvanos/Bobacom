package com.bobacom.backend.service.implementation;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bobacom.backend.dto.input.OperazioneMagazzinoRequest;
import com.bobacom.backend.dto.map.OperazioneMagazzinoMap;
import com.bobacom.backend.dto.output.OperazioneMagazzinoDTO;
import com.bobacom.backend.exceptions.AcademyException;
import com.bobacom.backend.model.Ingrediente;
import com.bobacom.backend.model.OperazioneMagazzino;
import com.bobacom.backend.repository.IIngredienteRepository;
import com.bobacom.backend.repository.IOperazioneMagazzinoRepository;
import com.bobacom.backend.service.interfaces.IOperazioneMagazzinoService;
import com.bobacom.backend.utilities.DateOperations;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class OperazioneMagazzinoImplementation implements IOperazioneMagazzinoService{
	private final IOperazioneMagazzinoRepository operazioneRepo;
	private final IIngredienteRepository ingredienteRepo;
	
	@Transactional
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
		ingrediente.setQuantitaStock(ingrediente.getQuantitaStock().add(operazione.getDeltaQuantita()));
		ingredienteRepo.save(ingrediente);
		log.debug("id operazione inserita: {}", insertedId);
	}

	@Transactional
	@Override
	public void delete(Integer id) throws Exception {
		OperazioneMagazzino operazione = operazioneRepo.findById(id).orElseThrow(
				() -> new AcademyException("no such operazione"));
		operazioneRepo.delete(operazione);
	}

	@Override
	public List<OperazioneMagazzinoDTO> find(	Integer id, 
												Integer idIngrediente,
												String dataMax,
												String dataMin,
												Boolean positivo) throws Exception {
		log.debug("prima del sospetto");
		List<OperazioneMagazzino> opList = operazioneRepo.searchByFilter(	id, 
																			idIngrediente,
																			DateOperations.stringToDate(dataMax), 
																			DateOperations.stringToDate(dataMin),
																			positivo);
		log.debug("dopo del sospetto");
		return opList.stream().map(o -> OperazioneMagazzinoMap.buildOperazioneMagazzinoDTO(o)).toList();
	}

}
