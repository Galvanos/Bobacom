package com.bobacom.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bobacom.backend.dto.input.IngredienteRequest;
import com.bobacom.backend.dto.input.OrdineRequest;
import com.bobacom.backend.dto.output.ResponseDTO;
import com.bobacom.backend.service.interfaces.IOrdineService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/rest/ordine")
public class OrdineController {
//	private Carrello carrello;
	private IOrdineService orderService;
	
	@PostMapping("create")
	public ResponseEntity<ResponseDTO> create(@RequestBody (required=true) OrdineRequest request) throws Exception{
		orderService.create(request);
		return ResponseEntity.ok(ResponseDTO.builder().msg("Ingrediente aggiunto").build());
	}
	
	@PatchMapping("update")
	public ResponseEntity<ResponseDTO> update(@RequestBody (required=true) OrdineRequest request) throws Exception{
		orderService.update(request);
		return ResponseEntity.ok(ResponseDTO.builder().msg("Ingrediente modificato").build());
	}
	
	@DeleteMapping("delete/{id}")
	public ResponseEntity<ResponseDTO> delete(@PathVariable (required=true) Integer id) throws Exception{
		orderService.delete(id);
		return ResponseEntity.ok(ResponseDTO.builder().msg("Ingrediente eliminato").build());
	}
	
}
