package com.bobacom.backend.service.interfaces;

import com.bobacom.backend.dto.input.ProdottoRequest;

public interface IProdottoService {
	void create(ProdottoRequest req) throws Exception;
	void update(ProdottoRequest req);
	void delete(Integer id);

}
