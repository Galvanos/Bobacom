package com.bobacom.backend.service.interfaces;

import java.util.List;

import com.bobacom.backend.dto.input.OrdineRequest;
import com.bobacom.backend.dto.output.OrdineDTO;

public interface IOrdineService {
	void create(OrdineRequest req) throws Exception;
	void update(OrdineRequest req) throws Exception;
	void delete(Integer id) throws Exception;
	List<OrdineDTO> list() throws Exception;
	List<OrdineDTO> listByUserId(Integer userId) throws Exception;
}
