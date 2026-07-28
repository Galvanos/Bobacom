package com.bobacom.backend.service.interfaces;

import java.util.List;

import com.bobacom.backend.dto.input.ComposizioneReq;
import com.bobacom.backend.dto.output.ComposizioneDTO;

public interface IComposizioneService {
	void create(ComposizioneReq req) throws Exception;
	void delete(Integer id) throws Exception;
	List<ComposizioneDTO> list(Integer idProdotto) throws Exception;
}
