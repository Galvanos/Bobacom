package com.bobacom.backend.controller;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bobacom.backend.dto.input.AllergeniRequest;
import com.bobacom.backend.dto.input.validation.ValidationGroups;
import com.bobacom.backend.dto.output.ResponseDTO;
import com.bobacom.backend.service.interfaces.IAllergeniService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/rest/allergeni")
public class AllergeniController {
	private final IAllergeniService allergeniService;
	
	@PostMapping("create")
	public ResponseEntity<ResponseDTO> create(@RequestBody (required=true) @Validated(ValidationGroups.Create.class) AllergeniRequest request) throws Exception{
		allergeniService.create(request);
		return ResponseEntity.ok(ResponseDTO.builder().msg("Allergene aggiunto").build());
	}
	
	@GetMapping("list")
	public ResponseEntity<Object> list() throws Exception{
		return ResponseEntity.ok(allergeniService.list());
	}
}
