package com.bobacom.backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Optional;

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

import com.bobacom.backend.dto.input.AddCreditReq;
import com.bobacom.backend.dto.input.LoginReq;
import com.bobacom.backend.dto.input.UtenteReq;
import com.bobacom.backend.dto.output.LoginDTO;
import com.bobacom.backend.dto.output.UtenteDTO;
import com.bobacom.backend.enums.Ruolo;
import com.bobacom.backend.repository.IUtenteRepository;
import com.bobacom.backend.service.interfaces.IUtenteService;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional // per questo test voglio i rollback ad ogni test, quindi transactional
public class CreditoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private IUtenteService utenteService;// il service serve per creare utenti
	
	@Value(value = "${app.credito.secret:}")
	private String creditoSecret;

	/**
	 * Creo un utente e vi aggiungo il credito
	 * @throws Exception 
	 */
	@Test
	public void testAddCredito() throws Exception {
		// creo un utente normale con privilegi utente
		UtenteDTO createdUtenteDTO = utenteService.create(
				UtenteReq.builder().username("utente").password("password").email("utente@example.com").build());

		//faccio login
		
		String loginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("utente").password("password").build());

		MvcResult mvcResult = mockMvc
				.perform(post("/rest/auth/login").content(loginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String responseString = mvcResult.getResponse().getContentAsString();

		LoginDTO loginDTO = objectMapper.readValue(responseString, LoginDTO.class);

		Assertions.assertThat(loginDTO.getAccessToken()).isNotBlank();
		Assertions.assertThat(loginDTO.getTokenType()).isEqualTo("Bearer");

		String accessToken = loginDTO.getAccessToken();
		
		BigDecimal originalCredito = createdUtenteDTO.getCredito();
		
		//per definizione, se null corrisponde a zero
		originalCredito = Optional.ofNullable(originalCredito).orElse(BigDecimal.ZERO);
		
		AddCreditReq addCreditoRequest = AddCreditReq.builder()
											.credit(BigDecimal.valueOf(100))
											.secret(creditoSecret)
											.userId(createdUtenteDTO.getId())
											.build();

		String addCreditoRequestJson = objectMapper.writeValueAsString(addCreditoRequest);
		
		MvcResult mvcResponse = mockMvc.perform(patch("/rest/credito/user/addCredito").content(addCreditoRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		
		String addCreditoResponseString = mvcResponse.getResponse().getContentAsString();

		UtenteDTO utenteAfterAddCredito = objectMapper.readValue(addCreditoResponseString, UtenteDTO.class);
		
		Assertions.assertThat(utenteAfterAddCredito.getId()).isEqualTo(createdUtenteDTO.getId());
		Assertions.assertThat(utenteAfterAddCredito.getUsername()).isEqualTo(createdUtenteDTO.getUsername());
		BigDecimal resultingCredito = utenteAfterAddCredito.getCredito();
		BigDecimal creditoIncrease = resultingCredito.subtract(originalCredito);
		Assertions.assertThat(creditoIncrease).isEqualTo(BigDecimal.valueOf(100));
		
	}
	
	
	/**
	 * Creo un utente e vi aggiungo il credito, ma lo identifico tramite la login, senza quindi fornire nel json l'id utente
	 * @throws Exception 
	 */
	@Test
	public void testAddCreditoByLogin() throws Exception {
		// creo un utente normale con privilegi utente
		UtenteDTO createdUtenteDTO = utenteService.create(
				UtenteReq.builder().username("utente").password("password").email("utente@example.com").build());

		//faccio login
		
		String loginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("utente").password("password").build());

		MvcResult mvcResult = mockMvc
				.perform(post("/rest/auth/login").content(loginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String responseString = mvcResult.getResponse().getContentAsString();

		LoginDTO loginDTO = objectMapper.readValue(responseString, LoginDTO.class);

		Assertions.assertThat(loginDTO.getAccessToken()).isNotBlank();
		Assertions.assertThat(loginDTO.getTokenType()).isEqualTo("Bearer");

		String accessToken = loginDTO.getAccessToken();
		
		BigDecimal originalCredito = createdUtenteDTO.getCredito();
		
		//per definizione, se null corrisponde a zero
		originalCredito = Optional.ofNullable(originalCredito).orElse(BigDecimal.ZERO);
		
		AddCreditReq addCreditoRequest = AddCreditReq.builder()
											.credit(BigDecimal.valueOf(100))
											.secret(creditoSecret)
											.build();

		String addCreditoRequestJson = objectMapper.writeValueAsString(addCreditoRequest);
		
		MvcResult mvcResponse = mockMvc.perform(patch("/rest/credito/user/addCredito").content(addCreditoRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		
		String addCreditoResponseString = mvcResponse.getResponse().getContentAsString();

		UtenteDTO utenteAfterAddCredito = objectMapper.readValue(addCreditoResponseString, UtenteDTO.class);
		
		Assertions.assertThat(utenteAfterAddCredito.getId()).isEqualTo(createdUtenteDTO.getId());
		Assertions.assertThat(utenteAfterAddCredito.getUsername()).isEqualTo(createdUtenteDTO.getUsername());
		BigDecimal resultingCredito = utenteAfterAddCredito.getCredito();
		BigDecimal creditoIncrease = resultingCredito.subtract(originalCredito);
		Assertions.assertThat(creditoIncrease).isEqualTo(BigDecimal.valueOf(100));
		
	}
	
	
	/**
	 * Creo due utenti utente e provo ad aggiungere credito al secondo utente da parte del primo utente, mi aspetto di fallire
	 * @throws Exception 
	 */
	@Test
	public void testAddCreditoOtherUser() throws Exception {
		// creo un utente normale con privilegi utente
		UtenteDTO createdUtenteDTO = utenteService.create(
				UtenteReq.builder().username("utente").password("password").email("utente@example.com").build());

		//faccio login
		
		String loginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("utente").password("password").build());

		MvcResult mvcResult = mockMvc
				.perform(post("/rest/auth/login").content(loginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String responseString = mvcResult.getResponse().getContentAsString();

		LoginDTO loginDTO = objectMapper.readValue(responseString, LoginDTO.class);

		Assertions.assertThat(loginDTO.getAccessToken()).isNotBlank();
		Assertions.assertThat(loginDTO.getTokenType()).isEqualTo("Bearer");

		String accessToken = loginDTO.getAccessToken();
		
		// creo un secondo utente normale con privilegi utente
		UtenteDTO altroUtenteDTO = utenteService.create(
				UtenteReq.builder().username("altro").password("altra_password").email("altro_utente@example.com").build());
		
		BigDecimal originalCredito = altroUtenteDTO.getCredito();
		
		//per definizione, se null corrisponde a zero
		originalCredito = Optional.ofNullable(originalCredito).orElse(BigDecimal.ZERO);
		
		AddCreditReq addCreditoRequest = AddCreditReq.builder()
											.credit(BigDecimal.valueOf(100))
											.secret(creditoSecret)
											.userId(altroUtenteDTO.getId())
											.build();

		String addCreditoRequestJson = objectMapper.writeValueAsString(addCreditoRequest);
		
		mockMvc.perform(patch("/rest/credito/user/addCredito").content(addCreditoRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
		
	}
	
	//*/////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Creo un utente amministrativo e vi aggiungo il credito
	 * @throws Exception 
	 */
	@Test
	public void testAddCreditoByAdmin() throws Exception {
		// creo un utente admin con privilegi admin
		UtenteDTO createdUtenteDTO = utenteService.create(
				UtenteReq.builder().username("admin").password("admin").email("admin@example.com")
				.ruolo(Ruolo.ADMIN).build());

		//faccio login
		
		String loginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("admin").password("admin").build());

		MvcResult mvcResult = mockMvc
				.perform(post("/rest/auth/login").content(loginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String responseString = mvcResult.getResponse().getContentAsString();

		LoginDTO loginDTO = objectMapper.readValue(responseString, LoginDTO.class);

		Assertions.assertThat(loginDTO.getAccessToken()).isNotBlank();
		Assertions.assertThat(loginDTO.getTokenType()).isEqualTo("Bearer");

		String accessToken = loginDTO.getAccessToken();
		
		BigDecimal originalCredito = createdUtenteDTO.getCredito();
		
		//per definizione, se null corrisponde a zero
		originalCredito = Optional.ofNullable(originalCredito).orElse(BigDecimal.ZERO);
		
		AddCreditReq addCreditoRequest = AddCreditReq.builder()
											.credit(BigDecimal.valueOf(100))
											.secret(creditoSecret)
											.userId(createdUtenteDTO.getId())
											.build();

		String addCreditoRequestJson = objectMapper.writeValueAsString(addCreditoRequest);
		
		MvcResult mvcResponse = mockMvc.perform(patch("/rest/credito/admin/addCredito").content(addCreditoRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		
		String addCreditoResponseString = mvcResponse.getResponse().getContentAsString();

		UtenteDTO utenteAfterAddCredito = objectMapper.readValue(addCreditoResponseString, UtenteDTO.class);
		
		Assertions.assertThat(utenteAfterAddCredito.getId()).isEqualTo(createdUtenteDTO.getId());
		Assertions.assertThat(utenteAfterAddCredito.getUsername()).isEqualTo(createdUtenteDTO.getUsername());
		BigDecimal resultingCredito = utenteAfterAddCredito.getCredito();
		BigDecimal creditoIncrease = resultingCredito.subtract(originalCredito);
		Assertions.assertThat(creditoIncrease).isEqualTo(BigDecimal.valueOf(100));
		
	}
	
	
	/**
	 * Creo un utente e vi aggiungo il credito, ma lo identifico tramite la login,
	 * senza quindi fornire nel json l'id utente
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddCreditoByLoginByAdmin() throws Exception {
		// creo un utente admin con privilegi admin
		UtenteDTO createdUtenteDTO = utenteService.create(UtenteReq.builder().username("admin").password("admin")
				.email("admin@example.com").ruolo(Ruolo.ADMIN).build());

		// faccio login

		String loginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("admin").password("admin").build());

		MvcResult mvcResult = mockMvc
				.perform(post("/rest/auth/login").content(loginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String responseString = mvcResult.getResponse().getContentAsString();

		LoginDTO loginDTO = objectMapper.readValue(responseString, LoginDTO.class);

		Assertions.assertThat(loginDTO.getAccessToken()).isNotBlank();
		Assertions.assertThat(loginDTO.getTokenType()).isEqualTo("Bearer");

		String accessToken = loginDTO.getAccessToken();

		BigDecimal originalCredito = createdUtenteDTO.getCredito();

		// per definizione, se null corrisponde a zero
		originalCredito = Optional.ofNullable(originalCredito).orElse(BigDecimal.ZERO);

		AddCreditReq addCreditoRequest = AddCreditReq.builder().credit(BigDecimal.valueOf(100)).secret(creditoSecret)
				.build();

		String addCreditoRequestJson = objectMapper.writeValueAsString(addCreditoRequest);

		MvcResult mvcResponse = mockMvc.perform(patch("/rest/credito/admin/addCredito").content(addCreditoRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		String addCreditoResponseString = mvcResponse.getResponse().getContentAsString();

		UtenteDTO utenteAfterAddCredito = objectMapper.readValue(addCreditoResponseString, UtenteDTO.class);

		Assertions.assertThat(utenteAfterAddCredito.getId()).isEqualTo(createdUtenteDTO.getId());
		Assertions.assertThat(utenteAfterAddCredito.getUsername()).isEqualTo(createdUtenteDTO.getUsername());
		BigDecimal resultingCredito = utenteAfterAddCredito.getCredito();
		BigDecimal creditoIncrease = resultingCredito.subtract(originalCredito);
		Assertions.assertThat(creditoIncrease).isEqualTo(BigDecimal.valueOf(100));

	}
	
	
	/**
	 * Creo un utente amministratore e un utente senza privilegi amministrativi e
	 * provo ad aggiungere credito al secondo utente da parte dellámministratore, mi
	 * aspetto che si possa fare
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddCreditoOtherUserByAdmin() throws Exception {
		// creo un utente admin con privilegi admin
		UtenteDTO createdUtenteDTO = utenteService.create(UtenteReq.builder().username("admin").password("admin")
				.email("admin@example.com").ruolo(Ruolo.ADMIN).build());

		// faccio login

		String loginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("admin").password("admin").build());

		MvcResult mvcResult = mockMvc
				.perform(post("/rest/auth/login").content(loginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String responseString = mvcResult.getResponse().getContentAsString();

		LoginDTO loginDTO = objectMapper.readValue(responseString, LoginDTO.class);

		Assertions.assertThat(loginDTO.getAccessToken()).isNotBlank();
		Assertions.assertThat(loginDTO.getTokenType()).isEqualTo("Bearer");

		String accessToken = loginDTO.getAccessToken();

		// creo un secondo utente normale con privilegi utente
		UtenteDTO altroUtenteDTO = utenteService.create(UtenteReq.builder().username("altro").password("altra_password")
				.email("altro_utente@example.com").build());

		BigDecimal originalCredito = altroUtenteDTO.getCredito();

		// per definizione, se null corrisponde a zero
		originalCredito = Optional.ofNullable(originalCredito).orElse(BigDecimal.ZERO);

		AddCreditReq addCreditoRequest = AddCreditReq.builder().credit(BigDecimal.valueOf(100)).secret(creditoSecret)
				.userId(altroUtenteDTO.getId()).build();

		String addCreditoRequestJson = objectMapper.writeValueAsString(addCreditoRequest);

		MvcResult mvcResponse = mockMvc.perform(patch("/rest/credito/user/addCredito").content(addCreditoRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		String addCreditoResponseString = mvcResponse.getResponse().getContentAsString();

		UtenteDTO utenteAfterAddCredito = objectMapper.readValue(addCreditoResponseString, UtenteDTO.class);

		Assertions.assertThat(utenteAfterAddCredito.getId()).isEqualTo(altroUtenteDTO.getId());
		Assertions.assertThat(utenteAfterAddCredito.getUsername()).isEqualTo(altroUtenteDTO.getUsername());
		BigDecimal resultingCredito = utenteAfterAddCredito.getCredito();
		BigDecimal creditoIncrease = resultingCredito.subtract(originalCredito);
		Assertions.assertThat(creditoIncrease).isEqualTo(BigDecimal.valueOf(100));

	}
	//TODO fare test con secret non valido
	//TODO riprendere qui con i test di add credito da parte dell'amministratore usando il servizio da utente e successivamente i test di decrease credito con enfasi anche sul provare a superare il limite
}
