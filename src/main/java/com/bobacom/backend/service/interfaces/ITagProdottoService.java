package com.bobacom.backend.service.interfaces;

import java.util.List;


import com.bobacom.backend.dto.input.TagRequest;
import com.bobacom.backend.dto.output.TagProdottoDTO;

public interface ITagProdottoService {
	void create(TagRequest req) throws Exception;
	void delete(Integer id) throws Exception;
	List<TagProdottoDTO> list() throws Exception;
	List<TagProdottoDTO> listOrdered() throws Exception;
	TagProdottoDTO getById(Integer id) throws Exception;
}
