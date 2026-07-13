package com.bobacom.backend.service.implementation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.bobacom.backend.dto.input.UtenteReq;
import com.bobacom.backend.dto.output.UtenteDTO;
import com.bobacom.backend.model.Utente;
import com.bobacom.backend.service.interfaces.IUtenteService;

public class UtenteImplementation implements IUtenteService {

	@Override
	public void create(UtenteReq req) throws Exception {
		req.setId(null);
		req.setCredito(BigDecimal.ZERO);//al momento alla creazione si impone credito zero, da capire se in futuro impostare un default eventualmente gestibile da admin
		Utente.builder()
		      .credito(req.getCredito())
		      .email(req.getEmail())
		      .indirizzo(req.getIndirizzo())
		      //TODO riprendere qui
		      .build();
		
	}

	@Override
	public void update(UtenteReq req) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UtenteDTO getById(Integer id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UtenteDTO> list() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Integer id) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
