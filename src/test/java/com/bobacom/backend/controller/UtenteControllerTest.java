package com.bobacom.backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.bobacom.backend.dto.output.UtenteDTO;
import com.bobacom.backend.enums.Ruolo;
import com.bobacom.backend.model.Utente;
import com.bobacom.backend.repository.IUtenteRepository;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional // per questo test voglio i rollback ad ogni test, quindi transactional
public class UtenteControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private IUtenteRepository utenteRepository;//il repository serve per creare utenti admin
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Value(value = "${app.credito.valoreDefault:0}")
	private BigDecimal creditoDefault;
	
	@Test
	public void testCreate() throws Exception {
		
		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password").email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
		
		Utente utenteCreato = utenteRepository.findByUsername("utente").orElseGet(() -> Assertions.fail("utente non trovato"));
		
		Assertions.assertThat(utenteCreato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteCreato.getPassword())).isTrue() ;
		Assertions.assertThat(utenteCreato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteCreato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteCreato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteCreato.getIndirizzo()).isBlank();
		
	}
	
	@Test
	public void testCreateDuplicate() throws Exception {
		
		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password").email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
		
		Utente utenteCreato = utenteRepository.findByUsername("utente").orElseGet(() -> Assertions.fail("utente non trovato"));
		
		Assertions.assertThat(utenteCreato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteCreato.getPassword())).isTrue() ;
		Assertions.assertThat(utenteCreato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteCreato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteCreato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteCreato.getIndirizzo()).isBlank();
		
		String utenteJSONDuplicato = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("altraPassword").email("altra_email@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSONDuplicato).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
		
	}
	
	//TODO continuare con tentativo di creare utente con credito e utente con ruolo admin

}
