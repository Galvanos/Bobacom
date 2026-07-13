package com.bobacom.backend.service.interfaces;

import com.bobacom.backend.dto.input.IngredienteRequest;

public interface IIngredienteService {
	void create(IngredienteRequest req) throws Exception;
	void update(IngredienteRequest req) throws Exception;
	void delete(Integer id) throws Exception;
}
