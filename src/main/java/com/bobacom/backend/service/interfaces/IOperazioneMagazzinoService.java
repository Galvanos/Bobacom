package com.bobacom.backend.service.interfaces;

import java.util.List;

import com.bobacom.backend.dto.input.OperazioneMagazzinoRequest;
import com.bobacom.backend.dto.output.OperazioneMagazzinoDTO;

public interface IOperazioneMagazzinoService {
	void create(OperazioneMagazzinoRequest req) throws Exception;
	void delete(Integer id) throws Exception;
	List<OperazioneMagazzinoDTO> list() throws Exception;
}
