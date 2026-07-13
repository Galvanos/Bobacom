package com.bobacom.backend.service.implementation;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bobacom.backend.dto.input.ProdottoRequest;
import com.bobacom.backend.exceptions.AcademyException;
import com.bobacom.backend.model.Composizione;
import com.bobacom.backend.model.Prodotto;
import com.bobacom.backend.repository.IProdottoRepository;
import com.bobacom.backend.service.interfaces.IProdottoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProdottoImplementation implements IProdottoService{

	private final IProdottoRepository prodottoRep;
	
	@Transactional
	@Override
	public void create(ProdottoRequest req) throws Exception {
		log.debug("create: {}", req);
		
		Prodotto p = new Prodotto();
		p.setNome(Optional.ofNullable(req.getNome())
		        .map(String::trim)
		        .filter(s -> !s.isEmpty())
		        .map(String::toUpperCase)
		        .orElseThrow(() -> new AcademyException("prodotto.no.nome")));

		p.setDescrizione(Optional.ofNullable(req.getDescrizione())
		        .map(String::trim)
		        .filter(s -> !s.isEmpty())
		        .map(String::toUpperCase)
		        .orElseThrow(() -> new AcademyException("prodotto.no.descr")));

		p.setImgUrl(Optional.ofNullable(req.getImgUrl())
		        .map(String::trim)
		        .filter(s -> !s.isEmpty())
		        .map(String::toUpperCase)
		        .orElseThrow(() -> new AcademyException("prodotto.no.imgurl")));
		
		p.setTag(req.getTag()); 
	    p.setPromozione(req.getPromozione());
	    
	    if (req.getComposizione() == null || req.getComposizione().isEmpty()) {
	        throw new AcademyException("prodotto.no.composizione");
	    }
	    
	    List<Composizione> compList = req.getComposizione();
	    for (Composizione c : compList) {
	        c.setProdotto(p);
	    }
	    p.setComposizione(compList);

	    prodottoRep.save(p);
		
	}

	@Transactional
	@Override
	public void update(ProdottoRequest req) {
		log.debug("update: {}", req);
		
		Prodotto p = prodottoRep.findById(req.getId())
				.orElseThrow(() -> new AcademyException("prodotto.ntfnd"));
		
		p.setNome(Optional.ofNullable(req.getNome())
		        .map(String::trim)
		        .filter(s -> !s.isEmpty())
		        .map(String::toUpperCase)
		        .orElse(p.getNome()));

		p.setDescrizione(Optional.ofNullable(req.getDescrizione())
		        .map(String::trim)
		        .filter(s -> !s.isEmpty())
		        .map(String::toUpperCase)
		        .orElse(p.getDescrizione()));

		p.setImgUrl(Optional.ofNullable(req.getImgUrl())
		        .map(String::trim)
		        .filter(s -> !s.isEmpty())
		        .map(String::toUpperCase)
		        .orElse(p.getImgUrl()));
		
		if(req.getTag() !=null) p.setTag(req.getTag()); 
		if(req.getPromozione() !=null) p.setPromozione(req.getPromozione());
	    
	    if (req.getComposizione() != null){
	    	req.getComposizione().forEach(c -> c.setProdotto(p));
	        p.setComposizione(req.getComposizione());
	    }
	    
	    prodottoRep.save(p);
	}

	@Transactional
	@Override
	public void delete(Integer id) {
		log.debug("delete: {}", id);
		
		Prodotto p = prodottoRep.findById(id)
				.orElseThrow(() -> new AcademyException("prodotto.ntfnd"));
		prodottoRep.delete(p);
		
	}

}
