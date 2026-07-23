package com.bobacom.backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.bobacom.backend.dto.input.UtenteReq;
import com.bobacom.backend.dto.input.validation.ValidationGroups;
import com.bobacom.backend.dto.output.ResponseDTO;
import com.bobacom.backend.dto.output.UtenteDTO;
import com.bobacom.backend.service.interfaces.IUtenteService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/rest/utente")
public class UtenteController {
	
	private final IUtenteService service;

	@PostMapping("/public/create")
	public ResponseEntity<ResponseDTO> createByUser(@RequestBody(required = true) @Validated(ValidationGroups.Create.class) UtenteReq  creatingUser) throws Exception {
		UtenteDTO created = service.createByUser(creatingUser);
		return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/user/getById").queryParam("id", created.getId()).build().toUri())
				.body(ResponseDTO.builder().msg("created").build());
		
	}
	
	@PostMapping("/admin/create")
	public ResponseEntity<ResponseDTO> createByAdmin(@RequestBody(required = true) @Validated(ValidationGroups.Create.class) UtenteReq  creatingUser) throws Exception {
		UtenteDTO created = service.create(creatingUser);
		return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/admin/getById").buildAndExpand(created.getId()).toUri())
				.body(ResponseDTO.builder().msg("created").build());	
	}
	
	@GetMapping("/user/getById")
	public  ResponseEntity<UtenteDTO> getByUser(@RequestParam(required = false) Integer id,Authentication authentication) throws Exception {
		String username = authentication.getName();
		UtenteDTO foundUser;
		if(id!=null) {
			foundUser = service.getByIdByUser(id);
		}else {
			foundUser = service.getByUsernameByUser(username);
		}
		foundUser.setPassword(null);//password annullata anche perché sarebbe un hash, non servirebbe a molto
		return ResponseEntity.ok(foundUser);
	}
	
	
	@GetMapping("/admin/getById")
	public  ResponseEntity<UtenteDTO> getByAdmin(@RequestParam(required = false) Integer id,Authentication authentication) throws Exception {
		String username = authentication.getName();
		UtenteDTO foundUser;
		if(id!=null) {
			foundUser = service.getById(id);
		}else {
			foundUser = service.getByUsername(username);
		}
		foundUser.setPassword(null);//password annullata anche perché sarebbe un hash, non servirebbe a molto
		return ResponseEntity.ok(foundUser);
	}
	
	
	@GetMapping("/user/getByUsername")
	public  ResponseEntity<UtenteDTO> getByUsernameByUser(@RequestParam(required = false) String username,Authentication authentication) throws Exception {
		username = Optional.ofNullable(username).orElse(authentication.getName());
		UtenteDTO byUsernameByUser = service.getByUsernameByUser(username);
		byUsernameByUser.setPassword(null);//password annullata anche perché sarebbe un hash, non servirebbe a molto
		return ResponseEntity.ok(byUsernameByUser);
	}
	
	
	@GetMapping("/admin/getByUsername")
	public  ResponseEntity<UtenteDTO> getByUsernameByAdmin(@RequestParam String username,Authentication authentication) throws Exception {
		username = Optional.ofNullable(username).orElse(authentication.getName());
		UtenteDTO byUsername = service.getByUsername(username);
		byUsername.setPassword(null);//password annullata anche perché sarebbe un hash, non servirebbe a molto
		return ResponseEntity.ok(byUsername);
	}
	
	@GetMapping("/admin/list")
	public  ResponseEntity<List<UtenteDTO>> list() throws Exception {
		List<UtenteDTO> list = service.list();
		//annullo le password visto che sarebbero solo degli hash
		list = list.stream().map(t -> t.setPassword(null)).toList();
		return ResponseEntity.ok(list);
	}
	
	@PatchMapping("/user/update")
	public ResponseEntity<ResponseDTO> updateByUser(@RequestBody(required = true) @Validated(ValidationGroups.Update.class) UtenteReq  updatingUser,Authentication authentication) throws Exception{
		String username = authentication.getName();
		if(updatingUser.getId() == null) {
			UtenteDTO byUsername = service.getByUsername(username);
			updatingUser.setId(byUsername.getId());
		}
		UtenteDTO updatedUser = service.updateByUser(updatingUser);
		return ResponseEntity.ok(new ResponseDTO("updated..."));
	}
	
	@PatchMapping("/admin/update")
	public ResponseEntity<ResponseDTO> updateByAdmin(@RequestBody(required = true) @Validated(ValidationGroups.Update.class) UtenteReq  updatingUser,Authentication authentication) throws Exception{
		String username = authentication.getName();
		if(updatingUser.getId() == null) {
			UtenteDTO byUsername = service.getByUsername(username);
			updatingUser.setId(byUsername.getId());
		}
		UtenteDTO updatedUser = service.update(updatingUser);
		return ResponseEntity.ok(new ResponseDTO("updated..."));
	}
	
	@DeleteMapping("/admin/delete/{id}")
	public ResponseEntity<ResponseDTO> delete(@PathVariable (required=true) Integer id) throws Exception{
		UtenteDTO updatedUser = service.delete(id);
		return ResponseEntity.ok(new ResponseDTO("updated..."));
	}
	
	
}
