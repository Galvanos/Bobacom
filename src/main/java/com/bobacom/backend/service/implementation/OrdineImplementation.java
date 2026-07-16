package com.bobacom.backend.service.implementation;

import org.springframework.stereotype.Service;

import com.bobacom.backend.dto.input.OrdineRequest;
import com.bobacom.backend.model.Ordine;
import com.bobacom.backend.model.Utente;
import com.bobacom.backend.repository.IOrdineRepository;
import com.bobacom.backend.repository.IUtenteRepository;
import com.bobacom.backend.service.interfaces.IOrdineService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class OrdineImplementation implements IOrdineService{
	private IUtenteRepository utenteRepo;
	private IOrdineRepository ordineRepo;

	@Transactional
	@Override
	public void create(OrdineRequest req) throws Exception{
		log.debug("create: {}", req);
		Utente utente = utenteRepo.findById(req.getUtente().getId())
				.orElseThrow(() -> new Exception("no user found with id: " + req.getUtente()));
				
		Ordine ordine = Ordine.builder()
				.prezzoTotale(req.getPrezzoTotale())
				.status(req.getStatus())
				.dataCreazione(req.getDataCreazione())
				.indirizzoDestinazione(req.getIndirizzoDestinazione())
				.utente(utente)
				.build();
		Integer idOrdine = ordineRepo.save(ordine).getId();
		log.debug("id ordine: {}", idOrdine);
		
	}

	@Transactional
	@Override
	public void delete(Integer id) throws Exception{
		log.debug("delete: {}", id);
		 Ordine order = ordineRepo.findById(id)
				 .orElseThrow(() -> new Exception("unable to find order id:" + id));
		 
		 ordineRepo.delete(order);
	}

}
