package com.bobacom.backend.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bobacom.backend.dto.input.PromozioneRequest;
import com.bobacom.backend.dto.output.PromozioneDTO;
import com.bobacom.backend.service.interfaces.IPromozioneService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PromozioneImplementation implements IPromozioneService{

	@Override
	public void create(PromozioneRequest req) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(PromozioneRequest req) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Integer id) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<PromozioneDTO> list() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PromozioneDTO getById(Integer id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
