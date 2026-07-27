package com.bobacom.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bobacom.backend.dto.input.AddCreditReq;
import com.bobacom.backend.dto.output.UtenteDTO;
import com.bobacom.backend.security.CustomUserDetailsService;
import com.bobacom.backend.security.interfaces.JwtService;
import com.bobacom.backend.service.interfaces.IUtenteService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("rest/credito")
public class CreditoController {

	private final IUtenteService utenteService;
	
	@PatchMapping("/user/addCredito")
	public ResponseEntity<UtenteDTO> addCreditoByUser(@RequestBody AddCreditReq req,Authentication authentication) throws Exception{
		Integer userId = req.getUserId();
		if(userId == null) {
			UtenteDTO byUsername = utenteService.getByUsername(authentication.getName());
			userId = byUsername.getId();
			req.setUserId(userId);
		}
		UtenteDTO updatedUtenteDTO = utenteService.addCreditByUser(req);
		UtenteDTO toReturn = UtenteDTO.builder()
				.credito(updatedUtenteDTO.getCredito())
				.id(updatedUtenteDTO.getId())
				.username(updatedUtenteDTO.getUsername())
				.build();
		return ResponseEntity.ok(toReturn);
	}
	
	
	@PatchMapping("/admin/addCredito")
	public ResponseEntity<UtenteDTO> addCreditoByAdmin(@RequestBody AddCreditReq req,Authentication authentication) throws Exception{
		Integer userId = req.getUserId();
		if(userId == null) {
			UtenteDTO byUsername = utenteService.getByUsername(authentication.getName());
			userId = byUsername.getId();
			req.setUserId(userId);
		}
		UtenteDTO updatedUtenteDTO = utenteService.addCredit(req);
		UtenteDTO toReturn = UtenteDTO.builder()
				.credito(updatedUtenteDTO.getCredito())
				.id(updatedUtenteDTO.getId())
				.username(updatedUtenteDTO.getUsername())
				.build();
		return ResponseEntity.ok(toReturn);
	}
	
}
