package com.bobacom.backend.service.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bobacom.backend.dto.input.ComposizioneReq;
import com.bobacom.backend.dto.input.ProdottoRequest;
import com.bobacom.backend.dto.map.ProdottoMap;
import com.bobacom.backend.dto.output.ProdottoDTO;
import com.bobacom.backend.exceptions.AcademyException;
import com.bobacom.backend.model.Composizione;
import com.bobacom.backend.model.Ingrediente;
import com.bobacom.backend.model.Prodotto;
import com.bobacom.backend.model.Promozione;
import com.bobacom.backend.model.TagProdotto;
import com.bobacom.backend.repository.IIngredienteRepository;
import com.bobacom.backend.repository.IProdottoRepository;
import com.bobacom.backend.repository.IPromozioneRepository;
import com.bobacom.backend.repository.ITagProdottoRepository;
import com.bobacom.backend.service.interfaces.IProdottoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProdottoImplementation implements IProdottoService{

	private final IProdottoRepository prodottoRep;
	private final ITagProdottoRepository tagRepo;
	private final IPromozioneRepository promoRepo;
	private final IIngredienteRepository ingRepo;
	
	@Transactional
	@Override
	public void create(ProdottoRequest req) throws Exception {
		log.debug("create: {}", req.toString());
		List<Promozione> promos = req.getPromozione().stream().map(id -> promoRepo.findById(id).orElseThrow(
				() -> new AcademyException("no such promozione with id:" + id))).toList();
		List<TagProdotto> tags = req.getTag().stream().map(id -> tagRepo.findById(id).orElseThrow(
				() -> new AcademyException("no such tag with id:" + id))).toList();
		
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
		        .orElseThrow(() -> new AcademyException("prodotto.no.imgurl")));
		
		p.setTag(tags.stream().collect(Collectors.toSet())); 
	    p.setPromozione(promos.stream().collect(Collectors.toSet()));
	    
	    for(ComposizioneReq compReq : req.getComposizione()) {
	    	Ingrediente ingrediente = ingRepo.findById(compReq.getIdIngrediente()).orElseThrow(
	    			() -> new AcademyException("prodotto.ingrediente.notfound"));
	    	Composizione composizione = Composizione.builder().ingrediente(ingrediente).quantita(compReq.getQuantita()).build();
	    	p.addComposizione(composizione);
	    }

	    p = prodottoRep.save(p);
	    log.debug("prodotto creato: {}", p.getId());
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
		
		if(req.getTag() !=null) {
			List<TagProdotto> tags = req.getTag().stream().map(id -> tagRepo.findById(id).orElseThrow(
					() -> new AcademyException("no such tag with id:" + id))).toList();
			p.setTag(tags.stream().collect(Collectors.toSet()));
		}
		if(req.getPromozione() !=null) {
			List<Promozione> promos = req.getPromozione().stream().map(id -> promoRepo.findById(id).orElseThrow(
					() -> new AcademyException("no such promozione with id:" + id))).toList();
			p.setPromozione(promos.stream().collect(Collectors.toSet()));
			}
	    
		List<Composizione> toRemove = new ArrayList<>();
	    	for(Composizione comp : p.getComposizione()) {
	    		if(!req.getComposizione().stream().anyMatch(compo -> compo.getIdIngrediente() == comp.getIngrediente().getId()))
	    			toRemove.add(comp);
	    	}
	    	for(Composizione comp : toRemove) {
	    		p.removeComposizione(comp);	    		
	    	}
	    	for(ComposizioneReq compReq : req.getComposizione()) {
		    	Ingrediente ingrediente = ingRepo.findById(compReq.getIdIngrediente()).orElseThrow(
		    			() -> new AcademyException("prodotto.ingrediente.notfound"));
		    	Composizione comp = p.getComposizione()	.stream()
		    											.filter(c -> Objects.equals(c.getIngrediente(), ingrediente))
		    											.findFirst().orElse(null);
		    	if(Objects.isNull(comp)){
		    		Composizione composizione = Composizione.builder().ingrediente(ingrediente).quantita(compReq.getQuantita()).build();
		    		p.addComposizione(composizione);
	    		} else {
	    			comp.setQuantita(compReq.getQuantita());
	    		}
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

	@Override
	public List<ProdottoDTO> list(String tag, Boolean isActive) throws Exception {
		log.debug("entered prodotto list with args: {} - {}", tag, isActive);
		List <Prodotto> lP = prodottoRep.searchByFilter(tag, isActive);
		return ProdottoMap.buildProdottoDTOList(lP.stream().collect(Collectors.toSet()));
	}

	@Override
	public ProdottoDTO getById(Integer id) throws Exception {
		log.debug("getById {}", id);
		Prodotto p = prodottoRep.findById(id)
				.orElseThrow(() -> new AcademyException("prodotto.ntfnd"));
		return ProdottoMap.buildProdottoDTO(p);
	}

}
