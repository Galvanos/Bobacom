package com.bobacom.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bobacom.backend.dto.input.ProdottoRequest;
import com.bobacom.backend.dto.output.ResponseDTO;
import com.bobacom.backend.service.interfaces.IProdottoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/rest/prodotto")
public class ProdottoController {
	private final IProdottoService prodottoS;
	
	@PostMapping("create")
	public ResponseEntity<ResponseDTO> create(@RequestBody (required = true) ProdottoRequest req) throws Exception{
		prodottoS.create(req);
		return ResponseEntity.ok(ResponseDTO.builder()
			.msg("created...")
			.build());
	}
	

	@PutMapping("update")
	public ResponseEntity<ResponseDTO> update(@RequestBody (required = true) ProdottoRequest req)  throws Exception{
		prodottoS.update(req);
		return ResponseEntity.ok(ResponseDTO.builder()
				.msg("updated...")
				.build());	
	}
	
	@DeleteMapping("delete/{id}")
	public ResponseEntity<ResponseDTO> delete(@PathVariable (required = true) Integer id) throws Exception {
		prodottoS.delete(id);
			return ResponseEntity.ok(ResponseDTO.builder()
					.msg("deleted...")
					.build());	
	}
	
	@GetMapping("/list")
	public ResponseEntity<Object> list() throws Exception{
		return ResponseEntity.ok(prodottoS.list());
	}
	
	@GetMapping("getById")
	public ResponseEntity<Object> getById(@RequestParam (required = true) Integer id) throws Exception{			
		return ResponseEntity.ok(prodottoS.getById(id));
	}


}
