package com.bobacom.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bobacom.backend.dto.input.CategoriaIngredienteRequest;
import com.bobacom.backend.dto.input.validation.ValidationGroups;
import com.bobacom.backend.dto.output.ResponseDTO;
import com.bobacom.backend.service.interfaces.ICategoriaService;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RestController
@RequestMapping("/rest/categoria_ingrediente")
public class CategoriaController {
	private final ICategoriaService catS;
	
	@PostMapping("create")
	public ResponseEntity<ResponseDTO> create(@RequestBody (required=true) @Validated(ValidationGroups.Create.class)
												CategoriaIngredienteRequest request) throws Exception{
		catS.create(request);
		return ResponseEntity.ok(ResponseDTO.builder().msg("Categoria aggiunta").build());
	}
	
	@GetMapping("list")
	public ResponseEntity<Object> list() throws Exception{
		return ResponseEntity.ok(catS.list());
	}
}
