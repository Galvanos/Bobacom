package com.bobacom.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bobacom.backend.dto.input.OperazioneMagazzinoRequest;
import com.bobacom.backend.dto.output.ResponseDTO;
import com.bobacom.backend.service.interfaces.IOperazioneMagazzinoService;
import com.bobacom.backend.utilities.DateOperations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/rest/operazionemagazzino")
public class OperazioneMagazzinoController {
	private final IOperazioneMagazzinoService operazioneService;
	
	@PostMapping("/create")
	public ResponseEntity<ResponseDTO> create(@RequestBody (required=true) OperazioneMagazzinoRequest request) throws Exception{
		operazioneService.create(request);
		return ResponseEntity.ok(ResponseDTO.builder().msg("operazione eseguita").build());
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResponseDTO> delete(@PathVariable (required=true) Integer id) throws Exception{
		operazioneService.delete(id);
		return ResponseEntity.ok(ResponseDTO.builder().msg("Ingrediente eliminato").build());
	}
	
	@GetMapping("/list")
	public ResponseEntity<Object> list(
			@RequestParam (required = false) Integer id,
			@RequestParam (required = false) Integer idIngrediente,
			@RequestParam (required = false) String dataMax,
			@RequestParam (required = false) String dataMin,
			@RequestParam (required = false) Boolean positivo
			)throws Exception{
		log.debug("dataMin: " + dataMin);
		log.debug("dataMax: " + dataMax);
		return ResponseEntity.ok(operazioneService.find(id, 
														idIngrediente, 
														dataMax, 
														dataMin, 
														positivo));
	}
}
