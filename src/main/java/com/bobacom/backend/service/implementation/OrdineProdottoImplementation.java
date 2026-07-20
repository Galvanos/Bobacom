package com.bobacom.backend.service.implementation;

import org.springframework.stereotype.Service;

import com.bobacom.backend.dto.input.OrdineProdottoReq;
import com.bobacom.backend.model.OrdineProdotto;
import com.bobacom.backend.model.Prodotto;
import com.bobacom.backend.repository.IOrdineProdottoRepository;
import com.bobacom.backend.repository.IProdottoRepository;
import com.bobacom.backend.service.interfaces.IOrdineProdottoService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdineProdottoImplementation implements IOrdineProdottoService{
    private IOrdineProdottoRepository opRepo;
    private IProdottoRepository pRepo;
	
	@Transactional
	@Override
	public void create(OrdineProdottoReq req) throws Exception {
		log.debug("create: {}", req);
		
		Prodotto prodotto = pRepo.findById(req.getProdotto_id())
				.orElseThrow(() -> new Exception("no product found with id: " + req.getProdotto_id()));
		
		OrdineProdotto op = new OrdineProdotto().builder()
				.prodotto(prodotto)
				.quantita(req.getQuantita())
				.build();
		
	}

}
