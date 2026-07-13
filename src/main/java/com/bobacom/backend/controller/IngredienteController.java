package com.bobacom.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bobacom.backend.dto.input.IngredienteRequest;
import com.bobacom.backend.dto.output.ResponseDTO;
import com.bobacom.backend.service.interfaces.IIngredienteService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/rest/ingrediente")
public class IngredienteController {
	private final IIngredienteService ingredienteService;
	
	@PostMapping("create")
	public ResponseEntity<ResponseDTO> create(@RequestBody (required=true) IngredienteRequest request) throws Exception{
		ingredienteService.create(request);
		return ResponseEntity.ok(ResponseDTO.builder().msg("Ingrediente aggiunto").build());
	}
}
