package com.bobacom.backend.service.interfaces;

import java.util.List;

import com.bobacom.backend.dto.input.PromozioneRequest;
import com.bobacom.backend.dto.output.PromozioneDTO;

public interface IPromozioneService {
	void create(PromozioneRequest req) throws Exception;
	void update(PromozioneRequest req);
	void delete(Integer id);
	
	List<PromozioneDTO> list() throws Exception;
	PromozioneDTO getById(Integer id) throws Exception;
}
