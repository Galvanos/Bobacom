package com.bobacom.backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.math.BigInteger;
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
import com.bobacom.backend.dto.input.DecreaseCreditReq;
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

	/**
	 * Creo un utente e vi aggiungo il credito
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddCredito() throws Exception {
		// creo un utente normale con privilegi utente
		UtenteDTO createdUtenteDTO = utenteService.create(
				UtenteReq.builder().username("utente").password("password").email("utente@example.com").build());

		// faccio login

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

		// per definizione, se null corrisponde a zero
		originalCredito = Optional.ofNullable(originalCredito).orElse(BigDecimal.ZERO);

		AddCreditReq addCreditoRequest = AddCreditReq.builder().credit(BigDecimal.valueOf(100))
				.userId(createdUtenteDTO.getId()).build();

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
	 * Creo un utente e vi aggiungo il credito, ma lo identifico tramite la login,
	 * senza quindi fornire nel json l'id utente
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddCreditoByLogin() throws Exception {
		// creo un utente normale con privilegi utente
		UtenteDTO createdUtenteDTO = utenteService.create(
				UtenteReq.builder().username("utente").password("password").email("utente@example.com").build());

		// faccio login

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

		// per definizione, se null corrisponde a zero
		originalCredito = Optional.ofNullable(originalCredito).orElse(BigDecimal.ZERO);

		AddCreditReq addCreditoRequest = AddCreditReq.builder().credit(BigDecimal.valueOf(100))
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
	 * Creo due utenti utente e provo ad aggiungere credito al secondo utente da
	 * parte del primo utente, mi aspetto di fallire
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddCreditoOtherUser() throws Exception {
		// creo un utente normale con privilegi utente
		UtenteDTO createdUtenteDTO = utenteService.create(
				UtenteReq.builder().username("utente").password("password").email("utente@example.com").build());

		// faccio login

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
		UtenteDTO altroUtenteDTO = utenteService.create(UtenteReq.builder().username("altro").password("altra_password")
				.email("altro_utente@example.com").build());

		BigDecimal originalCredito = altroUtenteDTO.getCredito();

		// per definizione, se null corrisponde a zero
		originalCredito = Optional.ofNullable(originalCredito).orElse(BigDecimal.ZERO);

		AddCreditReq addCreditoRequest = AddCreditReq.builder().credit(BigDecimal.valueOf(100))
				.userId(altroUtenteDTO.getId()).build();

		String addCreditoRequestJson = objectMapper.writeValueAsString(addCreditoRequest);

		mockMvc.perform(patch("/rest/credito/user/addCredito").content(addCreditoRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

	}


	/**
	 * Provo ad aggiungere credito a un utente inesistente, mi aspetto che fallisca
	 * anche perché è un altro utente
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddCreditoNotExisting() throws Exception {
		// creo un utente normale con privilegi utente
		UtenteDTO createdUtenteDTO = utenteService.create(
				UtenteReq.builder().username("utente").password("password").email("utente@example.com").build());

		// faccio login

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

		// per definizione, se null corrisponde a zero
		originalCredito = Optional.ofNullable(originalCredito).orElse(BigDecimal.ZERO);

		Assertions.assertThat(createdUtenteDTO.getId()).isNotEqualTo(Integer.MAX_VALUE);

		AddCreditReq addCreditoRequest = AddCreditReq.builder().credit(BigDecimal.valueOf(100))
				.userId(Integer.MAX_VALUE).build();

		String addCreditoRequestJson = objectMapper.writeValueAsString(addCreditoRequest);

		mockMvc.perform(patch("/rest/credito/user/addCredito").content(addCreditoRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

	}

	// */////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creo un utente amministrativo e vi aggiungo il credito
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddCreditoByAdmin() throws Exception {
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

		AddCreditReq addCreditoRequest = AddCreditReq.builder().credit(BigDecimal.valueOf(100))
				.userId(createdUtenteDTO.getId()).build();

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
	 * Creo un utente amministratore e vi aggiungo il credito, ma lo identifico
	 * tramite la login, senza quindi fornire nel json l'id utente
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

		AddCreditReq addCreditoRequest = AddCreditReq.builder().credit(BigDecimal.valueOf(100))
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
	 * provo ad aggiungere credito al secondo utente da parte dell'amministratore,
	 * mi aspetto che si possa fare
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

		AddCreditReq addCreditoRequest = AddCreditReq.builder().credit(BigDecimal.valueOf(100))
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


	/**
	 * Provo ad aggiungere credito a un utente inesistente da parte di un
	 * amministratore, mi aspetto che fallisca
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddCreditoNotExistingByAdmin() throws Exception {
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

		Assertions.assertThat(createdUtenteDTO.getId()).isNotEqualTo(Integer.MAX_VALUE);

		AddCreditReq addCreditoRequest = AddCreditReq.builder().credit(BigDecimal.valueOf(100))
				.userId(Integer.MAX_VALUE).build();

		String addCreditoRequestJson = objectMapper.writeValueAsString(addCreditoRequest);

		mockMvc.perform(patch("/rest/credito/admin/addCredito").content(addCreditoRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

	}

	// */////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// */////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creo un utente amministrativo e vi aggiungo il credito facendogli usare il
	 * servizio per utente
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddCreditoByAdminUsingUser() throws Exception {
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

		AddCreditReq addCreditoRequest = AddCreditReq.builder().credit(BigDecimal.valueOf(100))
				.userId(createdUtenteDTO.getId()).build();

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
	 * Creo un utente amministratore e vi aggiungo il credito, ma lo identifico
	 * tramite la login, senza quindi fornire nel json l'id utente usando il
	 * servizio per utente
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddCreditoByLoginByAdminUsingUser() throws Exception {
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

		AddCreditReq addCreditoRequest = AddCreditReq.builder().credit(BigDecimal.valueOf(100))
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
	 * Creo un utente amministratore e un utente senza privilegi amministrativi e
	 * provo ad aggiungere credito al secondo utente da parte dell'amministratore
	 * usando il servizio per utente, mi aspetto che si possa fare
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddCreditoOtherUserByAdminUsingUser() throws Exception {
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

		AddCreditReq addCreditoRequest = AddCreditReq.builder().credit(BigDecimal.valueOf(100))
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



	/**
	 * Provo ad aggiungere credito a un utente inesistente da parte di un
	 * amministratore usando il servizio per utente, mi aspetto che fallisca
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddCreditoNotExistingByAdminUsingUser() throws Exception {
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

		Assertions.assertThat(createdUtenteDTO.getId()).isNotEqualTo(Integer.MAX_VALUE);

		AddCreditReq addCreditoRequest = AddCreditReq.builder().credit(BigDecimal.valueOf(100))
				.userId(Integer.MAX_VALUE).build();

		String addCreditoRequestJson = objectMapper.writeValueAsString(addCreditoRequest);

		mockMvc.perform(patch("/rest/credito/admin/addCredito").content(addCreditoRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

	}

	// */////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// */////////////////////////////////////////////////////////////////////////////////////////////////

	// *///////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creo un utente e vi sottraggo il credito
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDecreaseCredito() throws Exception {
		// creo un utente normale con privilegi utente
		UtenteDTO createdUtenteDTO = utenteService.create(UtenteReq.builder().username("utente").password("password")
				.credito(BigDecimal.valueOf(100)).email("utente@example.com").build());

		// faccio login

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

		// per definizione, se null corrisponde a zero
		originalCredito = Optional.ofNullable(originalCredito).orElse(BigDecimal.ZERO);

		DecreaseCreditReq decreaseCreditoRequest = DecreaseCreditReq.builder().credit(BigDecimal.TEN)
				.userId(createdUtenteDTO.getId()).build();

		String decreaseCreditoRequestJson = objectMapper.writeValueAsString(decreaseCreditoRequest);

		MvcResult mvcResponse = mockMvc.perform(patch("/rest/credito/user/decreaseCredito")
				.content(decreaseCreditoRequestJson).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

		String decreaseCreditoResponseString = mvcResponse.getResponse().getContentAsString();

		UtenteDTO utenteAfterDecreaseCredito = objectMapper.readValue(decreaseCreditoResponseString, UtenteDTO.class);

		Assertions.assertThat(utenteAfterDecreaseCredito.getId()).isEqualTo(createdUtenteDTO.getId());
		Assertions.assertThat(utenteAfterDecreaseCredito.getUsername()).isEqualTo(createdUtenteDTO.getUsername());
		BigDecimal resultingCredito = utenteAfterDecreaseCredito.getCredito();
		BigDecimal creditoDecrease = originalCredito.subtract(resultingCredito);
		Assertions.assertThat(creditoDecrease).isEqualTo(BigDecimal.TEN);

	}

	/**
	 * Creo un utente e vi sottraggo il credito, ma lo identifico tramite la login,
	 * senza quindi fornire nel json l'id utente
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDecreaseCreditoByLogin() throws Exception {
		// creo un utente normale con privilegi utente
		UtenteDTO createdUtenteDTO = utenteService.create(UtenteReq.builder().username("utente").password("password")
				.credito(BigDecimal.valueOf(100)).email("utente@example.com").build());

		// faccio login

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

		// per definizione, se null corrisponde a zero
		originalCredito = Optional.ofNullable(originalCredito).orElse(BigDecimal.ZERO);

		DecreaseCreditReq decreaseCreditoRequest = DecreaseCreditReq.builder().credit(BigDecimal.TEN).build();

		String decreaseCreditoRequestJson = objectMapper.writeValueAsString(decreaseCreditoRequest);

		MvcResult mvcResponse = mockMvc.perform(patch("/rest/credito/user/decreaseCredito")
				.content(decreaseCreditoRequestJson).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

		String decreaseCreditoResponseString = mvcResponse.getResponse().getContentAsString();

		UtenteDTO utenteAfterDecreaseCredito = objectMapper.readValue(decreaseCreditoResponseString, UtenteDTO.class);

		Assertions.assertThat(utenteAfterDecreaseCredito.getId()).isEqualTo(createdUtenteDTO.getId());
		Assertions.assertThat(utenteAfterDecreaseCredito.getUsername()).isEqualTo(createdUtenteDTO.getUsername());
		BigDecimal resultingCredito = utenteAfterDecreaseCredito.getCredito();
		BigDecimal creditoDecrease = originalCredito.subtract(resultingCredito);
		Assertions.assertThat(creditoDecrease).isEqualTo(BigDecimal.TEN);

	}

	/**
	 * Creo due utenti utente e provo ad sottrarre credito al secondo utente da
	 * parte del primo utente, mi aspetto di fallire
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDecreaseCreditoOtherUser() throws Exception {
		// creo un utente normale con privilegi utente
		UtenteDTO createdUtenteDTO = utenteService.create(
				UtenteReq.builder().username("utente").password("password").email("utente@example.com").build());

		// faccio login

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
		UtenteDTO altroUtenteDTO = utenteService.create(UtenteReq.builder().username("altro").password("altra_password")
				.email("altro_utente@example.com").build());

		BigDecimal originalCredito = altroUtenteDTO.getCredito();

		// per definizione, se null corrisponde a zero
		originalCredito = Optional.ofNullable(originalCredito).orElse(BigDecimal.ZERO);

		DecreaseCreditReq decreaseCreditoRequest = DecreaseCreditReq.builder().credit(BigDecimal.TEN)
				.userId(altroUtenteDTO.getId()).build();

		String decreaseCreditoRequestJson = objectMapper.writeValueAsString(decreaseCreditoRequest);

		mockMvc.perform(patch("/rest/credito/user/decreaseCredito").content(decreaseCreditoRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

	}

	/**
	 * Provo ad sottrarre credito a un utente inesistente, mi aspetto che fallisca
	 * anche perché è un altro utente
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDecreaseCreditoNotExisting() throws Exception {
		// creo un utente normale con privilegi utente
		UtenteDTO createdUtenteDTO = utenteService.create(UtenteReq.builder().username("utente").password("password")
				.credito(BigDecimal.valueOf(100)).email("utente@example.com").build());

		// faccio login

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

		// per definizione, se null corrisponde a zero
		originalCredito = Optional.ofNullable(originalCredito).orElse(BigDecimal.ZERO);

		Assertions.assertThat(createdUtenteDTO.getId()).isNotEqualTo(Integer.MAX_VALUE);

		DecreaseCreditReq decreaseCreditoRequest = DecreaseCreditReq.builder().credit(BigDecimal.TEN)
				.userId(Integer.MAX_VALUE).build();

		String decreaseCreditoRequestJson = objectMapper.writeValueAsString(decreaseCreditoRequest);

		mockMvc.perform(patch("/rest/credito/user/decreaseCredito").content(decreaseCreditoRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

	}

	// */////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creo un utente amministrativo e vi sottraggo il credito
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDecreaseCreditoByAdmin() throws Exception {
		// creo un utente admin con privilegi admin
		UtenteDTO createdUtenteDTO = utenteService.create(UtenteReq.builder().username("admin").password("admin")
				.credito(BigDecimal.valueOf(100)).email("admin@example.com").ruolo(Ruolo.ADMIN).build());

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

		DecreaseCreditReq decreaseCreditoRequest = DecreaseCreditReq.builder().credit(BigDecimal.TEN)
				.userId(createdUtenteDTO.getId()).build();

		String decreaseCreditoRequestJson = objectMapper.writeValueAsString(decreaseCreditoRequest);

		MvcResult mvcResponse = mockMvc.perform(patch("/rest/credito/admin/decreaseCredito")
				.content(decreaseCreditoRequestJson).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

		String decreaseCreditoResponseString = mvcResponse.getResponse().getContentAsString();

		UtenteDTO utenteAfterDecreaseCredito = objectMapper.readValue(decreaseCreditoResponseString, UtenteDTO.class);

		Assertions.assertThat(utenteAfterDecreaseCredito.getId()).isEqualTo(createdUtenteDTO.getId());
		Assertions.assertThat(utenteAfterDecreaseCredito.getUsername()).isEqualTo(createdUtenteDTO.getUsername());
		BigDecimal resultingCredito = utenteAfterDecreaseCredito.getCredito();
		BigDecimal creditoDecrease = originalCredito.subtract(resultingCredito);
		Assertions.assertThat(creditoDecrease).isEqualTo(BigDecimal.TEN);

	}

	/**
	 * Creo un utente amministratore e vi sottraggo il credito, ma lo identifico
	 * tramite la login, senza quindi fornire nel json l'id utente
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDecreaseCreditoByLoginByAdmin() throws Exception {
		// creo un utente admin con privilegi admin
		UtenteDTO createdUtenteDTO = utenteService.create(UtenteReq.builder().username("admin").password("admin")
				.credito(BigDecimal.valueOf(100)).email("admin@example.com").ruolo(Ruolo.ADMIN).build());

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

		DecreaseCreditReq decreaseCreditoRequest = DecreaseCreditReq.builder().credit(BigDecimal.TEN).build();

		String decreaseCreditoRequestJson = objectMapper.writeValueAsString(decreaseCreditoRequest);

		MvcResult mvcResponse = mockMvc.perform(patch("/rest/credito/admin/decreaseCredito")
				.content(decreaseCreditoRequestJson).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

		String decreaseCreditoResponseString = mvcResponse.getResponse().getContentAsString();

		UtenteDTO utenteAfterDecreaseCredito = objectMapper.readValue(decreaseCreditoResponseString, UtenteDTO.class);

		Assertions.assertThat(utenteAfterDecreaseCredito.getId()).isEqualTo(createdUtenteDTO.getId());
		Assertions.assertThat(utenteAfterDecreaseCredito.getUsername()).isEqualTo(createdUtenteDTO.getUsername());
		BigDecimal resultingCredito = utenteAfterDecreaseCredito.getCredito();
		BigDecimal creditoDecrease = originalCredito.subtract(resultingCredito);
		Assertions.assertThat(creditoDecrease).isEqualTo(BigDecimal.TEN);

	}

	/**
	 * Creo un utente amministratore e un utente senza privilegi amministrativi e
	 * provo a sottrarre credito al secondo utente da parte dell'amministratore, mi
	 * aspetto che si possa fare
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDecreaseCreditoOtherUserByAdmin() throws Exception {
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

		DecreaseCreditReq decreaseCreditoRequest = DecreaseCreditReq.builder().credit(BigDecimal.TEN)
				.userId(altroUtenteDTO.getId()).build();

		String decreaseCreditoRequestJson = objectMapper.writeValueAsString(decreaseCreditoRequest);

		MvcResult mvcResponse = mockMvc.perform(patch("/rest/credito/admin/decreaseCredito")
				.content(decreaseCreditoRequestJson).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

		String decreaseCreditoResponseString = mvcResponse.getResponse().getContentAsString();

		UtenteDTO utenteAfterDecreaseCredito = objectMapper.readValue(decreaseCreditoResponseString, UtenteDTO.class);

		Assertions.assertThat(utenteAfterDecreaseCredito.getId()).isEqualTo(altroUtenteDTO.getId());
		Assertions.assertThat(utenteAfterDecreaseCredito.getUsername()).isEqualTo(altroUtenteDTO.getUsername());
		BigDecimal resultingCredito = utenteAfterDecreaseCredito.getCredito();
		BigDecimal creditoDecrease = originalCredito.subtract(resultingCredito);
		Assertions.assertThat(creditoDecrease).isEqualTo(BigDecimal.TEN);

	}

	/**
	 * Provo a sottrarre credito a un utente inesistente da parte di un
	 * amministratore, mi aspetto che fallisca
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDecreaseCreditoNotExistingByAdmin() throws Exception {
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

		Assertions.assertThat(createdUtenteDTO.getId()).isNotEqualTo(Integer.MAX_VALUE);

		DecreaseCreditReq decreaseCreditoRequest = DecreaseCreditReq.builder().credit(BigDecimal.TEN)
				.userId(Integer.MAX_VALUE).build();

		String decreaseCreditoRequestJson = objectMapper.writeValueAsString(decreaseCreditoRequest);

		mockMvc.perform(patch("/rest/credito/admin/decreaseCredito").content(decreaseCreditoRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

	}

	// */////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// */////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creo un utente amministrativo e vi sottraggo il credito facendogli usare il
	 * servizio per utente
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDecreaseCreditoByAdminUsingUser() throws Exception {
		// creo un utente admin con privilegi admin
		UtenteDTO createdUtenteDTO = utenteService.create(UtenteReq.builder().username("admin").password("admin")
				.credito(BigDecimal.valueOf(100)).email("admin@example.com").ruolo(Ruolo.ADMIN).build());

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

		DecreaseCreditReq decreaseCreditoRequest = DecreaseCreditReq.builder().credit(BigDecimal.TEN)
				.userId(createdUtenteDTO.getId()).build();

		String decreaseCreditoRequestJson = objectMapper.writeValueAsString(decreaseCreditoRequest);

		MvcResult mvcResponse = mockMvc.perform(patch("/rest/credito/user/decreaseCredito")
				.content(decreaseCreditoRequestJson).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

		String decreaseCreditoResponseString = mvcResponse.getResponse().getContentAsString();

		UtenteDTO utenteAfterDecreaseCredito = objectMapper.readValue(decreaseCreditoResponseString, UtenteDTO.class);

		Assertions.assertThat(utenteAfterDecreaseCredito.getId()).isEqualTo(createdUtenteDTO.getId());
		Assertions.assertThat(utenteAfterDecreaseCredito.getUsername()).isEqualTo(createdUtenteDTO.getUsername());
		BigDecimal resultingCredito = utenteAfterDecreaseCredito.getCredito();
		BigDecimal creditoDecrease = originalCredito.subtract(resultingCredito);
		Assertions.assertThat(creditoDecrease).isEqualTo(BigDecimal.TEN);

	}

	/**
	 * Creo un utente e vi sottraggo il credito, ma lo identifico tramite la login,
	 * senza quindi fornire nel json l'id utente usando il servizio per utente
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDecreaseCreditoByLoginByAdminUsingUser() throws Exception {
		// creo un utente admin con privilegi admin
		UtenteDTO createdUtenteDTO = utenteService.create(UtenteReq.builder().username("admin").password("admin")
				.credito(BigDecimal.valueOf(100)).email("admin@example.com").ruolo(Ruolo.ADMIN).build());

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

		DecreaseCreditReq decreaseCreditoRequest = DecreaseCreditReq.builder().credit(BigDecimal.TEN).build();

		String decreaseCreditoRequestJson = objectMapper.writeValueAsString(decreaseCreditoRequest);

		MvcResult mvcResponse = mockMvc.perform(patch("/rest/credito/user/decreaseCredito")
				.content(decreaseCreditoRequestJson).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

		String decreaseCreditoResponseString = mvcResponse.getResponse().getContentAsString();

		UtenteDTO utenteAfterDecreaseCredito = objectMapper.readValue(decreaseCreditoResponseString, UtenteDTO.class);

		Assertions.assertThat(utenteAfterDecreaseCredito.getId()).isEqualTo(createdUtenteDTO.getId());
		Assertions.assertThat(utenteAfterDecreaseCredito.getUsername()).isEqualTo(createdUtenteDTO.getUsername());
		BigDecimal resultingCredito = utenteAfterDecreaseCredito.getCredito();
		BigDecimal creditoDecrease = originalCredito.subtract(resultingCredito);
		Assertions.assertThat(creditoDecrease).isEqualTo(BigDecimal.TEN);

	}

	/**
	 * Creo un utente amministratore e un utente senza privilegi amministrativi e
	 * provo a sottrarre credito al secondo utente da parte dell'amministratore
	 * usando il servizio per utente, mi aspetto che si possa fare
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDecreaseCreditoOtherUserByAdminUsingUser() throws Exception {
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

		DecreaseCreditReq decreaseCreditoRequest = DecreaseCreditReq.builder().credit(BigDecimal.TEN)
				.userId(altroUtenteDTO.getId()).build();

		String decreaseCreditoRequestJson = objectMapper.writeValueAsString(decreaseCreditoRequest);

		MvcResult mvcResponse = mockMvc.perform(patch("/rest/credito/user/decreaseCredito")
				.content(decreaseCreditoRequestJson).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

		String decreaseCreditoResponseString = mvcResponse.getResponse().getContentAsString();

		UtenteDTO utenteAfterDecreaseCredito = objectMapper.readValue(decreaseCreditoResponseString, UtenteDTO.class);

		Assertions.assertThat(utenteAfterDecreaseCredito.getId()).isEqualTo(altroUtenteDTO.getId());
		Assertions.assertThat(utenteAfterDecreaseCredito.getUsername()).isEqualTo(altroUtenteDTO.getUsername());
		BigDecimal resultingCredito = utenteAfterDecreaseCredito.getCredito();
		BigDecimal creditoDecrease = originalCredito.subtract(resultingCredito);
		Assertions.assertThat(creditoDecrease).isEqualTo(BigDecimal.TEN);

	}

	/**
	 * Provo ad aggiungere credito a un utente inesistente da parte di un
	 * amministratore usando il servizio per utente, mi aspetto che fallisca
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDecreaseCreditoNotExistingByAdminUsingUser() throws Exception {
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

		Assertions.assertThat(createdUtenteDTO.getId()).isNotEqualTo(Integer.MAX_VALUE);

		DecreaseCreditReq decreaseCreditoRequest = DecreaseCreditReq.builder().credit(BigDecimal.TEN)
				.userId(Integer.MAX_VALUE).build();

		String decreaseCreditoRequestJson = objectMapper.writeValueAsString(decreaseCreditoRequest);

		mockMvc.perform(patch("/rest/credito/user/decreaseCredito").content(decreaseCreditoRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

	}

	// *///////////////////////////////////////////////////////////////////////////

	// TODO creare test con credito insufficiente per il decrease

	/**
	 * Creo un utente e vi sottraggo più credito di quanto dispone, mi aspetto che
	 * fallisca
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDecreaseInsufficientCredito() throws Exception {
		// creo un utente normale con privilegi utente
		UtenteDTO createdUtenteDTO = utenteService.create(UtenteReq.builder().username("utente").password("password")
				.credito(BigDecimal.valueOf(100)).email("utente@example.com").build());

		// faccio login

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

		// per definizione, se null corrisponde a zero
		originalCredito = Optional.ofNullable(originalCredito).orElse(BigDecimal.ZERO);

		BigDecimal subtractingCredit = originalCredito.add(BigDecimal.TEN);

		DecreaseCreditReq decreaseCreditoRequest = DecreaseCreditReq.builder().credit(subtractingCredit)
				.userId(createdUtenteDTO.getId()).build();

		String decreaseCreditoRequestJson = objectMapper.writeValueAsString(decreaseCreditoRequest);

		mockMvc.perform(patch("/rest/credito/user/decreaseCredito").content(decreaseCreditoRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

	}

	/**
	 * Creo un utente amministratore e un utente senza privilegi amministrativi e
	 * provo a sottrarre credito più credito di quello che dispone al secondo utente
	 * da parte dell'amministratore, mi aspetto fallisca
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDecreaseInsufficientCreditoOtherUserByAdmin() throws Exception {
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

		BigDecimal subtractingCredit = originalCredito.add(BigDecimal.TEN);

		DecreaseCreditReq decreaseCreditoRequest = DecreaseCreditReq.builder().credit(subtractingCredit)
				.userId(altroUtenteDTO.getId()).build();

		String decreaseCreditoRequestJson = objectMapper.writeValueAsString(decreaseCreditoRequest);

		mockMvc.perform(patch("/rest/credito/admin/decreaseCredito").content(decreaseCreditoRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

	}
}
