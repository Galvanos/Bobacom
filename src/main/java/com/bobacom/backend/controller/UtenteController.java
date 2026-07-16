package com.bobacom.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.bobacom.backend.dto.input.UtenteReq;
import com.bobacom.backend.dto.output.ResponseDTO;
import com.bobacom.backend.dto.output.UtenteDTO;
import com.bobacom.backend.service.interfaces.IUtenteService;

import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/rest/utente")
public class UtenteController {
	
	private final IUtenteService service;

	@PostMapping("/public/create")
	public ResponseEntity<ResponseDTO> createByUser(@RequestBody(required = true) UtenteReq  creatingUser) throws Exception {
		UtenteDTO created = service.createByUser(creatingUser);
		return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/user/getById").queryParam("id", created.getId()).build().toUri())
				.body(ResponseDTO.builder().msg("created").build());
		
	}
	
	@PostMapping("/admin/create")
	public ResponseEntity<ResponseDTO> createByAdmin(@RequestBody(required = true) UtenteReq  creatingUser) throws Exception {
		UtenteDTO created = service.create(creatingUser);
		return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/admin/getById").buildAndExpand(created.getId()).toUri())
				.body(ResponseDTO.builder().msg("created").build());	
	}
	
	@GetMapping("/user/getById")
	public  ResponseEntity<UtenteDTO> getByUser(@RequestParam Integer id) throws Exception {
		UtenteDTO byIdByUser = service.getByIdByUser(id);
		byIdByUser.setPassword(null);//password annullata anche perché sarebbe un hash, non servirebbe a molto
		return ResponseEntity.ok(byIdByUser);
	}
	
	
	@GetMapping("/admin/getById")
	public  ResponseEntity<UtenteDTO> getByAdmin(@RequestParam Integer id) throws Exception {
		UtenteDTO byId = service.getById(id);
		byId.setPassword(null);//password annullata anche perché sarebbe un hash, non servirebbe a molto
		return ResponseEntity.ok(byId);
	}
	
	
	
}
