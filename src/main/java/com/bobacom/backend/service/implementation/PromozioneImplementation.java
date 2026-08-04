package com.bobacom.backend.service.implementation;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.bobacom.backend.dto.input.PromozioneRequest;
import lombok.extern.slf4j.Slf4j;
import com.bobacom.backend.dto.output.PromozioneDTO;
import com.bobacom.backend.exceptions.AcademyException;
import com.bobacom.backend.model.Prodotto;
import com.bobacom.backend.model.Promozione;
import com.bobacom.backend.repository.IProdottoRepository;
import com.bobacom.backend.repository.IPromozioneRepository;
import com.bobacom.backend.service.interfaces.IPromozioneService;
import lombok.RequiredArgsConstructor;

@Slf4j
@RequiredArgsConstructor
@Service
public class PromozioneImplementation implements IPromozioneService{

	private final IPromozioneRepository promoRep;
	private final IProdottoRepository prodottoRep;
	
	@Override
	public void create(PromozioneRequest req) throws Exception {
		log.debug("promo create request: " + req.getProdotto().toString());
		Promozione promo = Promozione.builder().sconto(req.getSconto()).isActive(req.getIsActive()).build();
		for(Integer id: req.getProdotto()) {
			promo.addProdotto(prodottoRep.findById(id).orElseThrow(
					() -> new AcademyException("no such product")));
		}
		log.debug(promo.getProdotto().toArray()[0].toString());
		promoRep.save(promo);
	}

	@Override
	public void update(PromozioneRequest req) throws Exception {
		Promozione promo = promoRep.findById(req.getId())
				.orElseThrow(() -> new AcademyException("Nessuna promozione corrispondente all'id: "));
		Optional.ofNullable(req.getSconto()).ifPresent(promo::setSconto);
		Optional.ofNullable(req.getIsActive()).ifPresent(promo::setIsActive);
		
	    if (req.getProdotto() != null) {
	        Set<Integer> vecchiId = promo.getProdotto().stream()
	                .map(Prodotto::getId)
	                .collect(Collectors.toSet());

	        Set<Integer> nuoviId = new HashSet<>(req.getProdotto());

	        Set<Integer> daRimuovere = new HashSet<>(vecchiId);
	        daRimuovere.removeAll(nuoviId);

	        Set<Integer> daAggiungere = new HashSet<>(nuoviId);
	        daAggiungere.removeAll(vecchiId);

	        // rimuovo l'associazione dal lato owner (Prodotto)
	        promo.getProdotto().stream()
	                .filter(p -> daRimuovere.contains(p.getId()))
	                .forEach(p -> p.getPromozione().remove(promo));
	        promo.getProdotto().removeIf(p -> daRimuovere.contains(p.getId()));

	        // aggiungo la nuova associazione dal lato owner (Prodotto)
	        List<Prodotto> nuoviProdotti = prodottoRep.findAllById(daAggiungere);
	        for (Prodotto p : nuoviProdotti) {
	            p.getPromozione().add(promo);
	            promo.getProdotto().add(p);
	        }

	        prodottoRep.saveAll(nuoviProdotti); // persisto il lato owner della relazione
	    }

	}

	@Override
	public void delete(Integer id) throws Exception {
		Promozione promo = promoRep.findById(id).orElseThrow(() -> new AcademyException("Promozione non trovata"));
		promoRep.delete(promo);
	}

	@Override
	public List<PromozioneDTO> list() throws Exception {
	    return promoRep.findAll().stream()
	            .map(promo -> {
	                List<Integer> idProdotti = promo.getProdotto().stream()
	                        .map(p -> p.getId())
	                        .collect(Collectors.toList());

	                return PromozioneDTO.builder()
	                        .id(promo.getId())
	                        .sconto(promo.getSconto())
	                        .isActive(promo.getIsActive())
	                        .idProdotto(idProdotti)
	                        .build();
	            })
	            .collect(Collectors.toList());
	}

	@Override
	public PromozioneDTO getById(Integer id) throws Exception {
	    Promozione promo = promoRep.findById(id)
	            .orElseThrow(() -> new AcademyException("Nessuna promozione corrispondente all'id: "));

	    List<Integer> idProdotti = promo.getProdotto().stream()
	            .map(p -> p.getId())
	            .collect(Collectors.toList());

	    return PromozioneDTO.builder()
	            .id(promo.getId())
	            .sconto(promo.getSconto())
	            .isActive(promo.getIsActive())
	            .idProdotto(idProdotti)
	            .build();
	}
}
