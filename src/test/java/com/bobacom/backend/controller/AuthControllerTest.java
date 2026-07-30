package com.bobacom.backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.bobacom.backend.dto.input.LoginReq;
import com.bobacom.backend.dto.input.UtenteReq;
import com.bobacom.backend.dto.output.LoginDTO;
import com.bobacom.backend.model.Utente;
import com.bobacom.backend.repository.IUtenteRepository;
import com.bobacom.backend.service.interfaces.IUtenteService;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional // per questo test voglio i rollback ad ogni test, quindi transactional
public class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	
	@Autowired
	private IUtenteService utenteService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Value(value = "${app.credito.valoreDefault:0}")
	private BigDecimal creditoDefault;
	
	@Test
	public void loginUserTest() throws Exception {
		//creo un utente normale con privilegi utente
		utenteService.create(UtenteReq.builder().username("utente").password("password").email("utente@example.com").build());
		
		String loginReqJSON = objectMapper.writeValueAsString(LoginReq.builder().username("utente").password("password").build());
		
		MvcResult mvcResult = mockMvc.perform(post("/rest/auth/login").content(loginReqJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();
		
		String responseString = mvcResult.getResponse().getContentAsString();
		
		LoginDTO loginDTO = objectMapper.readValue(responseString, LoginDTO.class);
		
		Assertions.assertThat(loginDTO.getAccessToken()).isNotBlank();
		Assertions.assertThat(loginDTO.getTokenType()).isEqualTo("Bearer");
	}
}
