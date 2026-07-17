package com.bobacom.backend.service.interfaces;

import java.util.List;

import com.bobacom.backend.dto.input.IngredienteRequest;
import com.bobacom.backend.dto.output.IngredienteDTO;

public interface IIngredienteService {
	void create(IngredienteRequest req) throws Exception;
	void update(IngredienteRequest req) throws Exception;
	void delete(Integer id) throws Exception;
	List<IngredienteDTO> list() throws Exception;
	IngredienteDTO getById(Integer id) throws Exception; 
}
