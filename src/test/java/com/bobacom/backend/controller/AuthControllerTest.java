package com.bobacom.backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.bobacom.backend.dto.input.LoginReq;
import com.bobacom.backend.dto.input.UtenteReq;
import com.bobacom.backend.dto.output.LoginDTO;
import com.bobacom.backend.dto.output.UtenteDTO;
import com.bobacom.backend.enums.Ruolo;
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
	
	/**
	 * Test di login di un utente
	 * @throws Exception
	 */
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
	
	//non cambia a livello di login tra utente ed amministratore, per cui non si fa un test di login di un amministratore, casomai lo si testa per i dati su sè stesso
	
	/**
	 * Test sui dati iguardo l'utente stesso da parte di un utente
	 * @throws Exception
	 */
	@Test
	public void meUserTest() throws Exception {
		//creo un utente normale con privilegi utente
		utenteService.create(UtenteReq.builder().username("utente").password("password").email("utente@example.com").build());
		
		String loginReqJSON = objectMapper.writeValueAsString(LoginReq.builder().username("utente").password("password").build());
		
		MvcResult mvcResult = mockMvc.perform(post("/rest/auth/login").content(loginReqJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();
		
		String responseString = mvcResult.getResponse().getContentAsString();
		
		LoginDTO loginDTO = objectMapper.readValue(responseString, LoginDTO.class);
		
		Assertions.assertThat(loginDTO.getAccessToken()).isNotBlank();
		Assertions.assertThat(loginDTO.getTokenType()).isEqualTo("Bearer");
		
		String accessToken = loginDTO.getAccessToken();
		
		MvcResult mvcResultMe = mockMvc.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken)).andExpect(status().isOk()).andReturn();
		
		String responseStringMe = mvcResultMe.getResponse().getContentAsString();
		
		UtenteDTO utenteDTO = objectMapper.readValue(responseStringMe, UtenteDTO.class);
		
		Assertions.assertThat(utenteDTO.getUsername()).isEqualTo("utente");
		Assertions.assertThat(utenteDTO.getRuolo()).isEqualTo(Ruolo.UTENTE);
		//non restituisco la password che comunque sarebbe un hash
		Assertions.assertThat(utenteDTO.getPassword()).isBlank();
		Assertions.assertThat(utenteDTO.getCredito()).isNotNull();
		Assertions.assertThat(utenteDTO.getId()).isNotNull();
		
		
	}
}
