package com.bobacom.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bobacom.backend.dto.input.AllergeniRequest;
import com.bobacom.backend.dto.input.TagProdottoReq;
import com.bobacom.backend.dto.input.TagRequest;
import com.bobacom.backend.dto.input.validation.ValidationGroups;
import com.bobacom.backend.dto.output.ResponseDTO;
import com.bobacom.backend.service.interfaces.ITagProdottoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/rest/tag_prodotto")
@RestController
public class TagProdottoController {
	private final ITagProdottoService tagService;
	
	@PostMapping("create")
	public ResponseEntity<ResponseDTO> create(@RequestBody (required=true) @Validated(ValidationGroups.Create.class) TagRequest request) throws Exception{
		log.debug("entered create with req:" + request.toString());
		tagService.create(request);
		return ResponseEntity.ok(ResponseDTO.builder().msg("Tag aggiunto").build());
	}
	
	@GetMapping("list")
	public ResponseEntity<Object> list() throws Exception{
		return ResponseEntity.ok(tagService.list());
	}
	
	@GetMapping("listOrdered")
	public ResponseEntity<Object> listOrdered() throws Exception{
		return ResponseEntity.ok(tagService.listOrdered());
	}
}
