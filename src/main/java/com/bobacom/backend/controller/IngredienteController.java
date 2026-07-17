package com.bobacom.backend.controller;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bobacom.backend.dto.input.IngredienteRequest;
import com.bobacom.backend.dto.input.validation.ValidationGroups;
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
	public ResponseEntity<ResponseDTO> create(@RequestBody (required=true) @Validated(ValidationGroups.Create.class) IngredienteRequest request) throws Exception{
		ingredienteService.create(request);
		return ResponseEntity.ok(ResponseDTO.builder().msg("Ingrediente aggiunto").build());
	}
	
	@PatchMapping("update")
	public ResponseEntity<ResponseDTO> update(@RequestBody (required=true) @Validated(ValidationGroups.Update.class) IngredienteRequest request) throws Exception{
		ingredienteService.update(request);
		return ResponseEntity.ok(ResponseDTO.builder().msg("Ingrediente modificato").build());
	}
	
	@DeleteMapping("delete/{id}")
	public ResponseEntity<ResponseDTO> delete(@PathVariable (required=true) Integer id) throws Exception{
		ingredienteService.delete(id);
		return ResponseEntity.ok(ResponseDTO.builder().msg("Ingrediente eliminato").build());
	}
	
	@GetMapping("list")
	public ResponseEntity<Object> list(	@RequestParam (required = false) Integer id,
										@RequestParam (required = false) Integer idCategoria,
										@RequestParam (required = false) BigDecimal maxAmount) throws Exception{
		return ResponseEntity.ok(ingredienteService.list(id, idCategoria, maxAmount));
	}
}
