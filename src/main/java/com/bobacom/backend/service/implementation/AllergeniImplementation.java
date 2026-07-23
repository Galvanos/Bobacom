package com.bobacom.backend.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bobacom.backend.dto.input.AllergeniRequest;
import com.bobacom.backend.dto.map.AllergeniMap;
import com.bobacom.backend.dto.output.AllergeniDTO;
import com.bobacom.backend.model.Allergeni;
import com.bobacom.backend.repository.IAllergeniRepository;
import com.bobacom.backend.service.interfaces.IAllergeniService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class AllergeniImplementation implements IAllergeniService{
	private final IAllergeniRepository allergeniRepo;
	
	@Override
	public void create(AllergeniRequest req) throws Exception {
		Allergeni allergene = Allergeni.builder().nome(req.getNome()).urlIcona(req.getUrlIcona()).build();
		allergeniRepo.save(allergene);		
	}

	@Override
	public void delete(Integer id) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<AllergeniDTO> list() throws Exception {
		return  allergeniRepo.findAll().stream().map(a -> AllergeniMap.buildAllergeniDTO(a)).toList();
	}

	@Override
	public AllergeniDTO getById(Integer id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	

}
