package com.bobacom.backend.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bobacom.backend.dto.input.ComposizioneReq;
import com.bobacom.backend.dto.map.ComposizioneMap;
import com.bobacom.backend.dto.output.ComposizioneDTO;
import com.bobacom.backend.exceptions.AcademyException;
import com.bobacom.backend.model.Composizione;
import com.bobacom.backend.model.Ingrediente;
import com.bobacom.backend.model.Prodotto;
import com.bobacom.backend.repository.IComposizioneRepository;
import com.bobacom.backend.repository.IIngredienteRepository;
import com.bobacom.backend.repository.IProdottoRepository;
import com.bobacom.backend.service.interfaces.IComposizioneService;

import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComposizioneImplementation implements IComposizioneService{
	private final IComposizioneRepository compRepo;
	private final IIngredienteRepository ingRepo;
	private final IProdottoRepository prodRepo;
	
	@Transactional
	@Override
	public void create(ComposizioneReq req) throws Exception {
		Ingrediente ingrediente = ingRepo.findById(req.getIdIngrediente()).orElseThrow(
				() -> new AcademyException("no such ingrediente"));
		Prodotto prodotto = prodRepo.findById(req.getIdProdotto()).orElseThrow(
				() -> new AcademyException("no such prodotto"));
		Composizione composizione = Composizione.builder()	.ingrediente(ingrediente)
															.prodotto(prodotto)
															.quantita(req.getQuantita())
															.build();
		compRepo.save(composizione);
	}
	@Transactional
	@Override
	public void delete(Integer id) throws Exception {
		Composizione composizione = compRepo.findById(id).orElseThrow(() -> new AcademyException("no such composizione"));
		compRepo.delete(composizione);	
	}

	@Override
	public List<ComposizioneDTO> list(Integer idProdotto) throws Exception {
		return ComposizioneMap.buildComposizioneDTOList(compRepo.searchByFilter(idProdotto));
	}

}
