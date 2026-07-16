package com.bobacom.backend.service.interfaces;

import com.bobacom.backend.dto.input.OrdineRequest;

public interface IOrdineService {
	void create(OrdineRequest req) throws Exception;
	void update(OrdineRequest req) throws Exception;
	void delete(Integer id) throws Exception;
}
