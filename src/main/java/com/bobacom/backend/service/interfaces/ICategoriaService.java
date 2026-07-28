package com.bobacom.backend.service.interfaces;

import java.util.List;

import com.bobacom.backend.dto.input.CategoriaIngredienteRequest;
import com.bobacom.backend.dto.output.CategoriaIngredienteDTO;
import com.bobacom.backend.dto.output.IngredienteDTO;

public interface ICategoriaService {
	void create(CategoriaIngredienteRequest req) throws Exception;
	void update(CategoriaIngredienteRequest req) throws Exception;
	void delete(Integer id) throws Exception;
	List<CategoriaIngredienteDTO> list() throws Exception;
	CategoriaIngredienteDTO getById(Integer id) throws Exception;
}
