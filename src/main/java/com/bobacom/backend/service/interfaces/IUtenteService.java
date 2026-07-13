package com.bobacom.backend.service.interfaces;

import java.util.List;

import com.bobacom.backend.dto.input.UtenteReq;
import com.bobacom.backend.dto.output.UtenteDTO;

public interface IUtenteService {

	void create(UtenteReq req) throws Exception;
	void update(UtenteReq req) throws Exception;
	UtenteDTO getById(Integer id) throws Exception;
	List<UtenteDTO> list() throws Exception;
	void delete(Integer id) throws Exception;
}
