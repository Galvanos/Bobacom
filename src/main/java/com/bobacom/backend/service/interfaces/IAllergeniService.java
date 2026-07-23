package com.bobacom.backend.service.interfaces;

import java.util.List;

import com.bobacom.backend.dto.input.AllergeniRequest;
import com.bobacom.backend.dto.output.AllergeniDTO;

public interface IAllergeniService {
	void create(AllergeniRequest req) throws Exception;
	void delete(Integer id) throws Exception;
	List<AllergeniDTO> list() throws Exception;
	AllergeniDTO getById(Integer id) throws Exception;
}
