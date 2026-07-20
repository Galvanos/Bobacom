package com.bobacom.backend.service.implementation;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.bobacom.backend.dto.input.OrdineRequest;
import com.bobacom.backend.enums.StatoSpedizione;
import com.bobacom.backend.model.Ordine;
import com.bobacom.backend.model.Utente;
import com.bobacom.backend.repository.IOrdineProdottoRepository;
import com.bobacom.backend.repository.IOrdineRepository;
import com.bobacom.backend.repository.IUtenteRepository;
import com.bobacom.backend.service.interfaces.IOrdineService;
import com.bobacom.backend.utilities.DateOperations;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class OrdineImplementation implements IOrdineService{
	private final IUtenteRepository utenteRepo;
	private final IOrdineRepository ordineRepo;
	private final IOrdineProdottoRepository opRepo;

	@Transactional
	@Override
	public void create(OrdineRequest req) throws Exception{
		log.debug("create: {}", req);
		Utente utente = utenteRepo.findById(req.getUtente_id())
				.orElseThrow(() -> new Exception("no user found with id: " + req.getUtente_id()));
				
		Ordine ordine = Ordine.builder()
				.prezzoTotale(req.getPrezzoTotale())
				.dataCreazione(LocalDateTime.now())
				.indirizzoDestinazione(req.getIndirizzoDestinazione())
				.utente(utente)
				.build();
		
		if(req.getStatus() != null) {
			try {
				StatoSpedizione stato = StatoSpedizione.valueOf(req.getStatus().toUpperCase().trim());
				ordine.setStatus(stato);
			} catch (Exception e) {
				throw new Exception("Invalid status value: " + req.getStatus());
			}
		}
		utente.addOrdine(ordine);
		
		ordine = ordineRepo.save(ordine);
		log.debug("ordine: {}", ordine);
		
	}

	@Transactional
	@Override
	public void update(OrdineRequest req) throws Exception {
		log.debug("update: {}", req);
		Ordine ordine = ordineRepo.findById(req.getId())
				 .orElseThrow(() -> new Exception("unable to find order id:" + req.getId()));
		
		if(req.getStatus() != null) {
			try {
				StatoSpedizione stato = StatoSpedizione.valueOf(req.getStatus().toUpperCase().trim());
				ordine.setStatus(stato);
			} catch (Exception e) {
				throw new Exception("Invalid status value: " + req.getStatus());
			}
		}
	}
	
	@Transactional
	@Override
	public void delete(Integer id) throws Exception{
		log.debug("delete: {}", id);
		 Ordine order = ordineRepo.findById(id)
				 .orElseThrow(() -> new Exception("unable to find order id:" + id));
		 if(order.getUtente() != null) {
			 order.getUtente().removeOrdine(order);
		 }
		 ordineRepo.delete(order);
	}

}
