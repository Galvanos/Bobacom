package com.bobacom.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bobacom.backend.dto.input.OperazioneMagazzinoRequest;
import com.bobacom.backend.dto.output.ResponseDTO;
import com.bobacom.backend.service.interfaces.IOperazioneMagazzinoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/rest/operazionemagazzino")
public class OperazioneMagazzinoController {
	private final IOperazioneMagazzinoService operazioneService;
	
	@PostMapping("create")
	public ResponseEntity<ResponseDTO> create(@RequestBody (required=true) OperazioneMagazzinoRequest request) throws Exception{
		operazioneService.create(request);
		return ResponseEntity.ok(ResponseDTO.builder().msg("operazione eseguita").build());
	}
	
	@DeleteMapping("delete/{id}")
	public ResponseEntity<ResponseDTO> delete(@PathVariable (required=true) Integer id) throws Exception{
		operazioneService.delete(id);
		return ResponseEntity.ok(ResponseDTO.builder().msg("Ingrediente eliminato").build());
	}
}
