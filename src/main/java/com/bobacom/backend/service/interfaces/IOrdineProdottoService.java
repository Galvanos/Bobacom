package com.bobacom.backend.service.interfaces;

import com.bobacom.backend.dto.input.OrdineProdottoReq;

public interface IOrdineProdottoService {

	void create(OrdineProdottoReq req) throws Exception;
}
