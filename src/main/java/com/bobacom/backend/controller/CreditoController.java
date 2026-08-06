package com.bobacom.backend.controller;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bobacom.backend.dto.input.AddCreditReq;
import com.bobacom.backend.dto.input.DecreaseCreditReq;
import com.bobacom.backend.dto.output.StripedUtenteDTO;
import com.bobacom.backend.dto.output.UtenteDTO;
import com.bobacom.backend.security.CustomUserDetailsService;
import com.bobacom.backend.security.interfaces.JwtService;
import com.bobacom.backend.service.interfaces.IUtenteService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("rest/credito")
public class CreditoController {

	private final IUtenteService utenteService;
	
	@PatchMapping("/user/addCredito")
	public ResponseEntity<StripedUtenteDTO> addCreditoByUser(@RequestBody AddCreditReq req,Authentication authentication) throws Exception{
		Integer userId = req.getUserId();
		if(userId == null) {
			UtenteDTO byUsername = utenteService.getByUsername(authentication.getName());
			userId = byUsername.getId();
			req.setUserId(userId);
		}
		
		StripedUtenteDTO updatedUtenteDTO = utenteService.addCreditByUser(req);
		StripedUtenteDTO toReturn = StripedUtenteDTO.builder()
				.credito(updatedUtenteDTO.getCredito())
				.id(updatedUtenteDTO.getId())
				.username(updatedUtenteDTO.getUsername())
				.clientSecret(updatedUtenteDTO.getClientSecret())
				.build();
		return ResponseEntity.ok(toReturn);
	}
	
	
	@PatchMapping("/admin/addCredito")
	public ResponseEntity<StripedUtenteDTO> addCreditoByAdmin(@RequestBody AddCreditReq req,Authentication authentication) throws Exception{
		Integer userId = req.getUserId();
		if(userId == null) {
			UtenteDTO byUsername = utenteService.getByUsername(authentication.getName());
			userId = byUsername.getId();
			req.setUserId(userId);
		}
		StripedUtenteDTO updatedUtenteDTO = utenteService.addCredit(req);
		StripedUtenteDTO toReturn = StripedUtenteDTO.builder()
				.credito(updatedUtenteDTO.getCredito())
				.id(updatedUtenteDTO.getId())
				.username(updatedUtenteDTO.getUsername())
				.clientSecret(updatedUtenteDTO.getClientSecret())
				.build();
		return ResponseEntity.ok(toReturn);
	}
	
	
	@PatchMapping("/user/decreaseCredito")
	public ResponseEntity<UtenteDTO> decreaseCreditoByUser(@RequestBody DecreaseCreditReq req,Authentication authentication) throws Exception{
		Integer userId = req.getUserId();
		if(userId == null) {
			UtenteDTO byUsername = utenteService.getByUsername(authentication.getName());
			userId = byUsername.getId();
			req.setUserId(userId);
		}
		UtenteDTO updatedUtenteDTO = utenteService.decreaseCreditByUser(req);
		UtenteDTO toReturn = UtenteDTO.builder()
				.credito(updatedUtenteDTO.getCredito())
				.id(updatedUtenteDTO.getId())
				.username(updatedUtenteDTO.getUsername())
				.build();
		return ResponseEntity.ok(toReturn);
	}
	
	
	@PatchMapping("/admin/decreaseCredito")
	public ResponseEntity<UtenteDTO> decreaseCreditoByAdmin(@RequestBody DecreaseCreditReq req,Authentication authentication) throws Exception{
		Integer userId = req.getUserId();
		if(userId == null) {
			UtenteDTO byUsername = utenteService.getByUsername(authentication.getName());
			userId = byUsername.getId();
			req.setUserId(userId);
		}
		UtenteDTO updatedUtenteDTO = utenteService.decreaseCredit(req);
		UtenteDTO toReturn = UtenteDTO.builder()
				.credito(updatedUtenteDTO.getCredito())
				.id(updatedUtenteDTO.getId())
				.username(updatedUtenteDTO.getUsername())
				.build();
		return ResponseEntity.ok(toReturn);
	}
	
}
