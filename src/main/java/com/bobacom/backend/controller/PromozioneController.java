package com.bobacom.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bobacom.backend.dto.input.PromozioneRequest;
import com.bobacom.backend.dto.output.ResponseDTO;
import com.bobacom.backend.service.interfaces.IPromozioneService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/rest/promozione")
public class PromozioneController {
	private final IPromozioneService promozioneService;
	
	@PostMapping("create")
	public ResponseEntity<ResponseDTO> create(@RequestBody (required = true) PromozioneRequest request) throws Exception{
		promozioneService.create(request);
		return ResponseEntity.ok(ResponseDTO.builder().msg("Promozione aggiunta").build());
	}
	
	@PatchMapping("update")
	public ResponseEntity<ResponseDTO> update(@RequestBody (required = true) PromozioneRequest request) throws Exception{
		promozioneService.update(request);
		return ResponseEntity.ok(ResponseDTO.builder().msg("Promozione aggiornata").build());
	}
	
	@DeleteMapping("delete/{id}")
	public ResponseEntity<ResponseDTO> delete(@PathVariable (required = true) Integer id) throws Exception{
		promozioneService.delete(id);
		return ResponseEntity.ok(ResponseDTO.builder().msg("Promozione eliminata").build());
	}
	
	@GetMapping("/list")
	public ResponseEntity<Object> list() throws Exception{
		return ResponseEntity.ok(promozioneService.list());
	}
	
	@GetMapping("getId")
	public ResponseEntity<Object> getById(@RequestParam (required = true) Integer id) throws Exception{
		return ResponseEntity.ok(promozioneService.getById(id));
	}
}
