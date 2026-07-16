package com.bobacom.backend.service.interfaces;

import java.util.List;

import com.bobacom.backend.dto.input.ProdottoRequest;
import com.bobacom.backend.dto.output.ProdottoDTO;

public interface IProdottoService {
	void create(ProdottoRequest req) throws Exception;
	void update(ProdottoRequest req);
	void delete(Integer id);
	
	List<ProdottoDTO> list() throws Exception;
	ProdottoDTO getById(Integer id) throws Exception;

}
