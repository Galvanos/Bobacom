package com.bobacom.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
	private final IOrdineService orderService;
	
	@PostMapping("create")
	public ResponseEntity<ResponseDTO> create(@RequestBody (required=true) OrdineRequest request) throws Exception{
		log.debug("first item of order: " + request.getProdotti().getFirst().toString());
		orderService.create(request);
		return ResponseEntity.ok(ResponseDTO.builder().msg("Ordine aggiunto").build());
	}
	
	@PatchMapping("update")
	public ResponseEntity<ResponseDTO> update(@RequestBody (required=true) OrdineRequest request) throws Exception{
		orderService.update(request);
		return ResponseEntity.ok(ResponseDTO.builder().msg("Ordine modificato").build());
	}
	
	@DeleteMapping("delete/{id}")
	public ResponseEntity<ResponseDTO> delete(@PathVariable (required=true) Integer id) throws Exception{
		orderService.delete(id);
		return ResponseEntity.ok(ResponseDTO.builder().msg("Ordine eliminato").build());
	}
	@GetMapping("list")
	public ResponseEntity<Object> list() throws Exception{
		return ResponseEntity.ok(orderService.list());
	}
	@GetMapping("listByUser/{id}")
	public ResponseEntity<Object> list(@PathVariable (required=true) Integer id) throws Exception{
		return ResponseEntity.ok(orderService.listByUserId(id));
	}
	
}
