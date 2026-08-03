package com.bobacom.backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
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

import com.bobacom.backend.dto.input.LoginReq;
import com.bobacom.backend.dto.input.UtenteReq;
import com.bobacom.backend.dto.output.LoginDTO;
import com.bobacom.backend.dto.output.OrdineDTO;
import com.bobacom.backend.dto.output.UtenteDTO;
import com.bobacom.backend.enums.Ruolo;
import com.bobacom.backend.model.Utente;
import com.bobacom.backend.repository.IUtenteRepository;
import com.bobacom.backend.service.interfaces.IUtenteService;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional // per questo test voglio i rollback ad ogni test, quindi transactional
public class UtenteControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private IUtenteRepository utenteRepository;// il repository serve per verificare gli utenti creati

	@Autowired
	private IUtenteService utenteService;// il service serve per creare utenti amministrativi

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Value(value = "${app.credito.valoreDefault:0}")
	private BigDecimal creditoDefault;

	/**
	 * Creo un utente con i dati minimi
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreate() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(
				UtenteDTO.builder().username("utente").password("password").email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		Utente utenteCreato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteCreato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteCreato.getPassword())).isTrue();
		Assertions.assertThat(utenteCreato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteCreato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteCreato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteCreato.getIndirizzo()).isBlank();

	}

	/**
	 * Creo un utente con lo stesso username di un utente già creato, mi aspetto
	 * vada in errore la creazione del secondo utente
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateDuplicate() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(
				UtenteDTO.builder().username("utente").password("password").email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		Utente utenteCreato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteCreato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteCreato.getPassword())).isTrue();
		Assertions.assertThat(utenteCreato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteCreato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteCreato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteCreato.getIndirizzo()).isBlank();

		String utenteJSONDuplicato = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente")
				.password("altraPassword").email("altra_email@example.com").build());
		mockMvc.perform(
				post("/rest/utente/public/create").content(utenteJSONDuplicato).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

	}

	/**
	 * Creo un utente impostando il credito, mi aspetto che venga ignorato
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateUserWithCredit() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").credito(BigDecimal.valueOf(1000)).build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		Utente utenteCreato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteCreato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteCreato.getPassword())).isTrue();
		Assertions.assertThat(utenteCreato.getCredito()).isNotEqualTo(BigDecimal.valueOf(1000));
		Assertions.assertThat(utenteCreato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteCreato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteCreato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteCreato.getIndirizzo()).isBlank();

	}

	/**
	 * Creo un utente impostando come ruolo ADMIN, mi aspetto l'utente venga creato
	 * comunque con ruolo user
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateUserWithAdminRole() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").ruolo(Ruolo.ADMIN).build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		Utente utenteCreato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteCreato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteCreato.getPassword())).isTrue();
		Assertions.assertThat(utenteCreato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteCreato.getRuolo()).isNotEqualTo(Ruolo.ADMIN);
		Assertions.assertThat(utenteCreato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteCreato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteCreato.getIndirizzo()).isBlank();

	}

	/**
	 * Creo un utente impostando maxint come ID, mi aspetto venga ignorato
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateUserWithUserId() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").id(Integer.MAX_VALUE).build());// Uso max value, si assume che tra i vari
																			// test non raggiunga un id così alto
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		Utente utenteCreato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteCreato.getId()).isNotEqualTo(Integer.MAX_VALUE);
		Assertions.assertThat(utenteCreato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteCreato.getPassword())).isTrue();
		Assertions.assertThat(utenteCreato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteCreato.getRuolo()).isNotEqualTo(Ruolo.ADMIN);
		Assertions.assertThat(utenteCreato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteCreato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteCreato.getIndirizzo()).isBlank();

	}

	/**
	 * Creo un utente impostando l'indirizzo, mi aspetto funzioni
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateUserWithAddress() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").indirizzo("Via Torino 1").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		Utente utenteCreato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteCreato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteCreato.getPassword())).isTrue();
		Assertions.assertThat(utenteCreato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteCreato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteCreato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteCreato.getIndirizzo()).isNotBlank();
		Assertions.assertThat(utenteCreato.getIndirizzo()).isEqualTo("Via Torino 1");

	}

	/**
	 * Creo un utente impostando senza username, mi aspetto fallisca
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateUserWithoutUsername() throws Exception {

		String utenteJSON = objectMapper
				.writeValueAsString(UtenteDTO.builder().password("password").email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

	}

	/**
	 * Creo un utente impostando senza password, mi aspetto fallisca
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateUserWithoutPassword() throws Exception {

		String utenteJSON = objectMapper
				.writeValueAsString(UtenteDTO.builder().username("utente").email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		Assertions.assertThat(utenteRepository.existsByUsername("utente")).isFalse();

	}

	/**
	 * Creo un utente impostando senza email, mi aspetto fallisca
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateUserWithoutEmail() throws Exception {

		String utenteJSON = objectMapper
				.writeValueAsString(UtenteDTO.builder().username("utente").password("password").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		Assertions.assertThat(utenteRepository.existsByUsername("utente")).isFalse();

	}

	/**
	 * Creo un utente con i dati minimi da parte dell'amministratore
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateByAdmin() throws Exception {

		// creo un utente amministratore per svolgere le operazioni
		utenteService.create(UtenteReq.builder().username("admin").password("admin_password").email("admin@example.com")
				.ruolo(Ruolo.ADMIN).build());

		String loginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("admin").password("admin_password").build());

		MvcResult mvcResult = mockMvc
				.perform(post("/rest/auth/login").content(loginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String responseString = mvcResult.getResponse().getContentAsString();

		LoginDTO loginDTO = objectMapper.readValue(responseString, LoginDTO.class);

		String accessToken = loginDTO.getAccessToken();

		String utenteJSON = objectMapper.writeValueAsString(
				UtenteDTO.builder().username("utente").password("password").email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/admin/create").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.content(utenteJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());

		Utente utenteCreato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteCreato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteCreato.getPassword())).isTrue();
		Assertions.assertThat(utenteCreato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteCreato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteCreato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteCreato.getIndirizzo()).isBlank();

	}

	/**
	 * Creo un utente con i dati minimi usando l'endpoind amministrativo ma con un
	 * utente senza privilegi amministrativi, mi aspetto di venire bloccato
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateInAdminEndpointByNotAdmin() throws Exception {

		// creo un utente non amministrativo che farà login ma non potrà fare
		// l'operazione
		utenteService.create(UtenteReq.builder().username("non_admin").password("non_admin_password")
				.email("non_admin@example.com").build());

		String loginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("non_admin").password("non_admin_password").build());

		MvcResult mvcResult = mockMvc
				.perform(post("/rest/auth/login").content(loginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String responseString = mvcResult.getResponse().getContentAsString();

		LoginDTO loginDTO = objectMapper.readValue(responseString, LoginDTO.class);

		String accessToken = loginDTO.getAccessToken();

		String utenteJSON = objectMapper.writeValueAsString(
				UtenteDTO.builder().username("utente").password("password").email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/admin/create").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.content(utenteJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());

		Assertions.assertThat(utenteRepository.existsByUsername("utente")).isFalse();

	}

	/**
	 * Creo un utente con i dati minimi usando l'endpoind amministrativo senza fare
	 * login, mi aspetto di venire bloccato
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateInAdminEndpointWithoutLogin() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(
				UtenteDTO.builder().username("utente").password("password").email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/admin/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());

		Assertions.assertThat(utenteRepository.existsByUsername("utente")).isFalse();

	}

	/**
	 * Creo un utente con lo stesso username di un utente già creato da parte di un
	 * amministratore, mi aspetto vada in errore la creazione del secondo utente
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateDuplicateByAdmin() throws Exception {

		// creo un utente amministratore per svolgere le operazioni
		utenteService.create(UtenteReq.builder().username("admin").password("admin_password").email("admin@example.com")
				.ruolo(Ruolo.ADMIN).build());

		String loginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("admin").password("admin_password").build());

		MvcResult mvcResult = mockMvc
				.perform(post("/rest/auth/login").content(loginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String responseString = mvcResult.getResponse().getContentAsString();

		LoginDTO loginDTO = objectMapper.readValue(responseString, LoginDTO.class);

		String accessToken = loginDTO.getAccessToken();

		String utenteJSON = objectMapper.writeValueAsString(
				UtenteDTO.builder().username("utente").password("password").email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/admin/create").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.content(utenteJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());

		Utente utenteCreato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteCreato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteCreato.getPassword())).isTrue();
		Assertions.assertThat(utenteCreato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteCreato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteCreato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteCreato.getIndirizzo()).isBlank();

		String utenteJSONDuplicato = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente")
				.password("altraPassword").email("altra_email@example.com").build());
		mockMvc.perform(post("/rest/utente/admin/create").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.content(utenteJSONDuplicato).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

	}

	/**
	 * Creo un utente impostando il credito da parte dell'amministratore, mi aspetto
	 * che venga impostato il credito voluto dall'amministratore
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateUserWithCreditByAdmin() throws Exception {

		// creo un utente amministratore per svolgere le operazioni
		utenteService.create(UtenteReq.builder().username("admin").password("admin_password").email("admin@example.com")
				.ruolo(Ruolo.ADMIN).build());

		String loginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("admin").password("admin_password").build());

		MvcResult mvcResult = mockMvc
				.perform(post("/rest/auth/login").content(loginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String responseString = mvcResult.getResponse().getContentAsString();

		LoginDTO loginDTO = objectMapper.readValue(responseString, LoginDTO.class);

		String accessToken = loginDTO.getAccessToken();

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").credito(BigDecimal.valueOf(1000)).build());
		mockMvc.perform(post("/rest/utente/admin/create").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.content(utenteJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());

		Utente utenteCreato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteCreato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteCreato.getPassword())).isTrue();
		Assertions.assertThat(utenteCreato.getCredito()).isEqualTo(BigDecimal.valueOf(1000));
		Assertions.assertThat(utenteCreato.getCredito()).isNotEqualTo(creditoDefault);
		Assertions.assertThat(utenteCreato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteCreato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteCreato.getIndirizzo()).isBlank();

	}

	/**
	 * Creo un utente impostando come ruolo ADMIN da parte di un amministratore, mi
	 * aspetto l'utente venga creato con ruolo ADMIN perché creato da amministratore
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateUserWithAdminRoleByAdmin() throws Exception {

		// creo un utente amministratore per svolgere le operazioni
		utenteService.create(UtenteReq.builder().username("admin").password("admin_password").email("admin@example.com")
				.ruolo(Ruolo.ADMIN).build());

		String loginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("admin").password("admin_password").build());

		MvcResult mvcResult = mockMvc
				.perform(post("/rest/auth/login").content(loginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String responseString = mvcResult.getResponse().getContentAsString();

		LoginDTO loginDTO = objectMapper.readValue(responseString, LoginDTO.class);

		String accessToken = loginDTO.getAccessToken();

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").ruolo(Ruolo.ADMIN).build());
		mockMvc.perform(post("/rest/utente/admin/create").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.content(utenteJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());

		Utente utenteCreato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteCreato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteCreato.getPassword())).isTrue();
		Assertions.assertThat(utenteCreato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteCreato.getRuolo()).isEqualTo(Ruolo.ADMIN);
		Assertions.assertThat(utenteCreato.getRuolo()).isNotEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteCreato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteCreato.getIndirizzo()).isBlank();

	}

	/**
	 * Creo un utente impostando maxint come ID da parte di un amministratore, mi
	 * aspetto venga ignorato
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateUserWithUserIdByAdmin() throws Exception {

		// creo un utente amministratore per svolgere le operazioni
		utenteService.create(UtenteReq.builder().username("admin").password("admin_password").email("admin@example.com")
				.ruolo(Ruolo.ADMIN).build());

		String loginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("admin").password("admin_password").build());

		MvcResult mvcResult = mockMvc
				.perform(post("/rest/auth/login").content(loginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String responseString = mvcResult.getResponse().getContentAsString();

		LoginDTO loginDTO = objectMapper.readValue(responseString, LoginDTO.class);

		String accessToken = loginDTO.getAccessToken();

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").id(Integer.MAX_VALUE).build());// Uso max value, si assume che tra i vari
																			// test non raggiunga un id così alto
		mockMvc.perform(post("/rest/utente/admin/create").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.content(utenteJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());

		Utente utenteCreato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteCreato.getId()).isNotEqualTo(Integer.MAX_VALUE);
		Assertions.assertThat(utenteCreato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteCreato.getPassword())).isTrue();
		Assertions.assertThat(utenteCreato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteCreato.getRuolo()).isNotEqualTo(Ruolo.ADMIN);
		Assertions.assertThat(utenteCreato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteCreato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteCreato.getIndirizzo()).isBlank();

	}

	/**
	 * Creo un utente impostando l'indirizzo da parte di un amministratore, mi
	 * aspetto funzioni
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateUserWithAddressByAdmin() throws Exception {

		// creo un utente amministratore per svolgere le operazioni
		utenteService.create(UtenteReq.builder().username("admin").password("admin_password").email("admin@example.com")
				.ruolo(Ruolo.ADMIN).build());

		String loginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("admin").password("admin_password").build());

		MvcResult mvcResult = mockMvc
				.perform(post("/rest/auth/login").content(loginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String responseString = mvcResult.getResponse().getContentAsString();

		LoginDTO loginDTO = objectMapper.readValue(responseString, LoginDTO.class);

		String accessToken = loginDTO.getAccessToken();

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").indirizzo("Via Torino 1").build());
		mockMvc.perform(post("/rest/utente/admin/create").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.content(utenteJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());

		Utente utenteCreato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteCreato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteCreato.getPassword())).isTrue();
		Assertions.assertThat(utenteCreato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteCreato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteCreato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteCreato.getIndirizzo()).isNotBlank();
		Assertions.assertThat(utenteCreato.getIndirizzo()).isEqualTo("Via Torino 1");

	}

	/**
	 * Creo un utente impostando senza username da parte di un amministratore, mi
	 * aspetto fallisca
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateUserWithoutUsernameByAdmin() throws Exception {

		// creo un utente amministratore per svolgere le operazioni
		utenteService.create(UtenteReq.builder().username("admin").password("admin_password").email("admin@example.com")
				.ruolo(Ruolo.ADMIN).build());

		String loginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("admin").password("admin_password").build());

		MvcResult mvcResult = mockMvc
				.perform(post("/rest/auth/login").content(loginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String responseString = mvcResult.getResponse().getContentAsString();

		LoginDTO loginDTO = objectMapper.readValue(responseString, LoginDTO.class);

		String accessToken = loginDTO.getAccessToken();

		String utenteJSON = objectMapper
				.writeValueAsString(UtenteDTO.builder().password("password").email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/admin/create").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.content(utenteJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());

	}

	/**
	 * Creo un utente impostando senza password da parte di un amministratore, mi
	 * aspetto fallisca
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateUserWithoutPasswordByAdmin() throws Exception {

		// creo un utente amministratore per svolgere le operazioni
		utenteService.create(UtenteReq.builder().username("admin").password("admin_password").email("admin@example.com")
				.ruolo(Ruolo.ADMIN).build());

		String loginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("admin").password("admin_password").build());

		MvcResult mvcResult = mockMvc
				.perform(post("/rest/auth/login").content(loginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String responseString = mvcResult.getResponse().getContentAsString();

		LoginDTO loginDTO = objectMapper.readValue(responseString, LoginDTO.class);

		String accessToken = loginDTO.getAccessToken();

		String utenteJSON = objectMapper
				.writeValueAsString(UtenteDTO.builder().username("utente").email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/admin/create").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.content(utenteJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());

		Assertions.assertThat(utenteRepository.existsByUsername("utente")).isFalse();

	}

	/**
	 * Creo un utente impostando senza email da parte di un amministratore, mi
	 * aspetto fallisca
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateUserWithoutEmailByAdmin() throws Exception {

		// creo un utente amministratore per svolgere le operazioni
		utenteService.create(UtenteReq.builder().username("admin").password("admin_password").email("admin@example.com")
				.ruolo(Ruolo.ADMIN).build());

		String loginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("admin").password("admin_password").build());

		MvcResult mvcResult = mockMvc
				.perform(post("/rest/auth/login").content(loginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String responseString = mvcResult.getResponse().getContentAsString();

		LoginDTO loginDTO = objectMapper.readValue(responseString, LoginDTO.class);

		String accessToken = loginDTO.getAccessToken();

		String utenteJSON = objectMapper
				.writeValueAsString(UtenteDTO.builder().username("utente").password("password").build());
		mockMvc.perform(post("/rest/utente/admin/create").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.content(utenteJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());

		Assertions.assertThat(utenteRepository.existsByUsername("utente")).isFalse();

	}

	/**
	 * Creo un utente con i dati minimi da parte dell'amministratore, usando il
	 * servizio public, mi aspetto funzioni
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateByAdminUsingPublic() throws Exception {

		// creo un utente amministratore per svolgere le operazioni
		utenteService.create(UtenteReq.builder().username("admin").password("admin_password").email("admin@example.com")
				.ruolo(Ruolo.ADMIN).build());

		String loginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("admin").password("admin_password").build());

		MvcResult mvcResult = mockMvc
				.perform(post("/rest/auth/login").content(loginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String responseString = mvcResult.getResponse().getContentAsString();

		LoginDTO loginDTO = objectMapper.readValue(responseString, LoginDTO.class);

		String accessToken = loginDTO.getAccessToken();

		String utenteJSON = objectMapper.writeValueAsString(
				UtenteDTO.builder().username("utente").password("password").email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.content(utenteJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());

		Utente utenteCreato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteCreato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteCreato.getPassword())).isTrue();
		Assertions.assertThat(utenteCreato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteCreato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteCreato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteCreato.getIndirizzo()).isBlank();

	}

	/**
	 * Creo un utente con lo stesso username di un utente già creato da parte di un
	 * amministratore usando il servizio pubblico, mi aspetto vada in errore la
	 * creazione del secondo utente
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateDuplicateByAdminUsingPublic() throws Exception {

		// creo un utente amministratore per svolgere le operazioni
		utenteService.create(UtenteReq.builder().username("admin").password("admin_password").email("admin@example.com")
				.ruolo(Ruolo.ADMIN).build());

		String loginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("admin").password("admin_password").build());

		MvcResult mvcResult = mockMvc
				.perform(post("/rest/auth/login").content(loginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String responseString = mvcResult.getResponse().getContentAsString();

		LoginDTO loginDTO = objectMapper.readValue(responseString, LoginDTO.class);

		String accessToken = loginDTO.getAccessToken();

		String utenteJSON = objectMapper.writeValueAsString(
				UtenteDTO.builder().username("utente").password("password").email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.content(utenteJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());

		Utente utenteCreato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteCreato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteCreato.getPassword())).isTrue();
		Assertions.assertThat(utenteCreato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteCreato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteCreato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteCreato.getIndirizzo()).isBlank();

		String utenteJSONDuplicato = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente")
				.password("altraPassword").email("altra_email@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.content(utenteJSONDuplicato).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

	}

	/**
	 * Creo un utente impostando il credito da parte dell'amministratore usando il
	 * servizio public, poiché il servizio è qurello public mi aspetto che il
	 * credito venga forzato al default
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateUserWithCreditByAdminUsingPublic() throws Exception {

		// creo un utente amministratore per svolgere le operazioni
		utenteService.create(UtenteReq.builder().username("admin").password("admin_password").email("admin@example.com")
				.ruolo(Ruolo.ADMIN).build());

		String loginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("admin").password("admin_password").build());

		MvcResult mvcResult = mockMvc
				.perform(post("/rest/auth/login").content(loginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String responseString = mvcResult.getResponse().getContentAsString();

		LoginDTO loginDTO = objectMapper.readValue(responseString, LoginDTO.class);

		String accessToken = loginDTO.getAccessToken();

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").credito(BigDecimal.valueOf(1000)).build());
		mockMvc.perform(post("/rest/utente/public/create").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.content(utenteJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());

		Utente utenteCreato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteCreato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteCreato.getPassword())).isTrue();
		Assertions.assertThat(utenteCreato.getCredito()).isNotEqualTo(BigDecimal.valueOf(1000));
		Assertions.assertThat(utenteCreato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteCreato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteCreato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteCreato.getIndirizzo()).isBlank();

	}

	/**
	 * Creo un utente impostando come ruolo ADMIN da parte di un amministratore
	 * usando il servizio public, mi aspetto l'utente venga creato con ruolo UTENTE
	 * perché il servizio public forza il ruolo
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateUserWithAdminRoleByAdminUsingPublic() throws Exception {

		// creo un utente amministratore per svolgere le operazioni
		utenteService.create(UtenteReq.builder().username("admin").password("admin_password").email("admin@example.com")
				.ruolo(Ruolo.ADMIN).build());

		String loginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("admin").password("admin_password").build());

		MvcResult mvcResult = mockMvc
				.perform(post("/rest/auth/login").content(loginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String responseString = mvcResult.getResponse().getContentAsString();

		LoginDTO loginDTO = objectMapper.readValue(responseString, LoginDTO.class);

		String accessToken = loginDTO.getAccessToken();

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").ruolo(Ruolo.ADMIN).build());
		mockMvc.perform(post("/rest/utente/public/create").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.content(utenteJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());

		Utente utenteCreato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteCreato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteCreato.getPassword())).isTrue();
		Assertions.assertThat(utenteCreato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteCreato.getRuolo()).isNotEqualTo(Ruolo.ADMIN);
		Assertions.assertThat(utenteCreato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteCreato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteCreato.getIndirizzo()).isBlank();

	}

	/**
	 * Creo un utente impostando maxint come ID da parte di un amministratore usando
	 * il servizio public, mi aspetto venga ignorato
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateUserWithUserIdByAdminUsingPublic() throws Exception {

		// creo un utente amministratore per svolgere le operazioni
		utenteService.create(UtenteReq.builder().username("admin").password("admin_password").email("admin@example.com")
				.ruolo(Ruolo.ADMIN).build());

		String loginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("admin").password("admin_password").build());

		MvcResult mvcResult = mockMvc
				.perform(post("/rest/auth/login").content(loginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String responseString = mvcResult.getResponse().getContentAsString();

		LoginDTO loginDTO = objectMapper.readValue(responseString, LoginDTO.class);

		String accessToken = loginDTO.getAccessToken();

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").id(Integer.MAX_VALUE).build());// Uso max value, si assume che tra i vari
																			// test non raggiunga un id così alto
		mockMvc.perform(post("/rest/utente/public/create").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.content(utenteJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());

		Utente utenteCreato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteCreato.getId()).isNotEqualTo(Integer.MAX_VALUE);
		Assertions.assertThat(utenteCreato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteCreato.getPassword())).isTrue();
		Assertions.assertThat(utenteCreato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteCreato.getRuolo()).isNotEqualTo(Ruolo.ADMIN);
		Assertions.assertThat(utenteCreato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteCreato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteCreato.getIndirizzo()).isBlank();

	}

	/**
	 * Creo un utente impostando l'indirizzo da parte di un amministratore usando il
	 * servizio public, mi aspetto funzioni
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateUserWithAddressByAdminUsingPublic() throws Exception {

		// creo un utente amministratore per svolgere le operazioni
		utenteService.create(UtenteReq.builder().username("admin").password("admin_password").email("admin@example.com")
				.ruolo(Ruolo.ADMIN).build());

		String loginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("admin").password("admin_password").build());

		MvcResult mvcResult = mockMvc
				.perform(post("/rest/auth/login").content(loginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String responseString = mvcResult.getResponse().getContentAsString();

		LoginDTO loginDTO = objectMapper.readValue(responseString, LoginDTO.class);

		String accessToken = loginDTO.getAccessToken();

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").indirizzo("Via Torino 1").build());
		mockMvc.perform(post("/rest/utente/public/create").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.content(utenteJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());

		Utente utenteCreato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteCreato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteCreato.getPassword())).isTrue();
		Assertions.assertThat(utenteCreato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteCreato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteCreato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteCreato.getIndirizzo()).isNotBlank();
		Assertions.assertThat(utenteCreato.getIndirizzo()).isEqualTo("Via Torino 1");

	}

	/**
	 * Creo un utente impostando senza username da parte di un amministratore usando
	 * il servizio public, mi aspetto fallisca
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateUserWithoutUsernameByAdminUsingPublic() throws Exception {

		// creo un utente amministratore per svolgere le operazioni
		utenteService.create(UtenteReq.builder().username("admin").password("admin_password").email("admin@example.com")
				.ruolo(Ruolo.ADMIN).build());

		String loginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("admin").password("admin_password").build());

		MvcResult mvcResult = mockMvc
				.perform(post("/rest/auth/login").content(loginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String responseString = mvcResult.getResponse().getContentAsString();

		LoginDTO loginDTO = objectMapper.readValue(responseString, LoginDTO.class);

		String accessToken = loginDTO.getAccessToken();

		String utenteJSON = objectMapper
				.writeValueAsString(UtenteDTO.builder().password("password").email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.content(utenteJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());

	}

	/**
	 * Creo un utente impostando senza password da parte di un amministratore usando
	 * il servizio public, mi aspetto fallisca
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateUserWithoutPasswordByAdminUsingPublic() throws Exception {

		// creo un utente amministratore per svolgere le operazioni
		utenteService.create(UtenteReq.builder().username("admin").password("admin_password").email("admin@example.com")
				.ruolo(Ruolo.ADMIN).build());

		String loginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("admin").password("admin_password").build());

		MvcResult mvcResult = mockMvc
				.perform(post("/rest/auth/login").content(loginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String responseString = mvcResult.getResponse().getContentAsString();

		LoginDTO loginDTO = objectMapper.readValue(responseString, LoginDTO.class);

		String accessToken = loginDTO.getAccessToken();

		String utenteJSON = objectMapper
				.writeValueAsString(UtenteDTO.builder().username("utente").email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.content(utenteJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());

		Assertions.assertThat(utenteRepository.existsByUsername("utente")).isFalse();

	}

	/**
	 * Creo un utente impostando senza email da parte di un amministratore usando il
	 * servizio public, mi aspetto fallisca
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateUserWithoutEmailByAdminUsingPublic() throws Exception {

		// creo un utente amministratore per svolgere le operazioni
		utenteService.create(UtenteReq.builder().username("admin").password("admin_password").email("admin@example.com")
				.ruolo(Ruolo.ADMIN).build());

		String loginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("admin").password("admin_password").build());

		MvcResult mvcResult = mockMvc
				.perform(post("/rest/auth/login").content(loginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String responseString = mvcResult.getResponse().getContentAsString();

		LoginDTO loginDTO = objectMapper.readValue(responseString, LoginDTO.class);

		String accessToken = loginDTO.getAccessToken();

		String utenteJSON = objectMapper
				.writeValueAsString(UtenteDTO.builder().username("utente").password("password").build());
		mockMvc.perform(post("/rest/utente/public/create").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.content(utenteJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());

		Assertions.assertThat(utenteRepository.existsByUsername("utente")).isFalse();

	}

	/**
	 * Creo un utente e gli faccio aggiornare la sua email basandomi sul login, mi
	 * aspetto funzioni
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateEmailTestRecognizingByLogin() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(
				UtenteDTO.builder().username("utente").password("password").email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		UtenteReq updateRequest = UtenteReq.builder().email("email_aggiornata@example.com").build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// recupero direttamente l'utente da repository
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("email_aggiornata@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();
	}

	/**
	 * Creo un utente e gli faccio aggiornare la sua email basandomi sull'id utente
	 * ma dovrebbe essere lo stesso utente loggato, mi aspetto che funzioni
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateEmailTestRecognizingById() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(
				UtenteDTO.builder().username("utente").password("password").email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		// recupero i dati utente con il webservice me
		MvcResult mvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isOk()).andReturn();

		String responseStringMe = mvcResultMe.getResponse().getContentAsString();

		UtenteDTO utenteDTO = objectMapper.readValue(responseStringMe, UtenteDTO.class);

		UtenteReq updateRequest = UtenteReq.builder().email("email_aggiornata@example.com").id(utenteDTO.getId())
				.build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// recupero direttamente l'utente da repository
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("email_aggiornata@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();
	}

	/**
	 * Creo due utenti senza privilegi amministrativi e provo ad aggiornare l'email
	 * dell'altro basandomi sull'id utente, mi aspetto vada in errore
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateEmailTestRecognizingByIdWrongId() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(
				UtenteDTO.builder().username("utente").password("password").email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		String altroUtenteJSON = objectMapper.writeValueAsString(
				UtenteDTO.builder().username("altro").password("altra_password").email("altro@example.com").build());
		mockMvc.perform(
				post("/rest/utente/public/create").content(altroUtenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login dell'altro utente

		String altroLoginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("altro").password("altra_password").build());

		MvcResult altroMvcResult = mockMvc
				.perform(post("/rest/auth/login").content(altroLoginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String altroResponseString = altroMvcResult.getResponse().getContentAsString();

		LoginDTO altroLoginDTO = objectMapper.readValue(altroResponseString, LoginDTO.class);

		Assertions.assertThat(altroLoginDTO.getAccessToken()).isNotBlank();
		Assertions.assertThat(altroLoginDTO.getTokenType()).isEqualTo("Bearer");

		String altroAccessToken = altroLoginDTO.getAccessToken();

		// recupero i dati utente con il webservice me
		MvcResult altroMvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + altroAccessToken))
				.andExpect(status().isOk()).andReturn();

		String altroResponseStringMe = altroMvcResultMe.getResponse().getContentAsString();

		UtenteDTO altroUtenteDTO = objectMapper.readValue(altroResponseStringMe, UtenteDTO.class);

		// faccio la richiesta usando come id quello dell'altro utente
		UtenteReq updateRequest = UtenteReq.builder().email("email_aggiornata@example.com").id(altroUtenteDTO.getId())
				.build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		// come token di autenticazione uso quello di utente
		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

		// recupero direttamente l' utente da repository mi aspetto non sia cambiato
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isNotEqualTo("email_aggiornata@example.com");
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();

		// recupero direttamente l'altro utente da repository mi aspetto non sia
		// cambiato
		Utente altroUtenteAggiornato = utenteRepository.findByUsername("altro")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(altroUtenteAggiornato.getUsername()).isEqualTo("altro");
		Assertions.assertThat(passwordEncoder.matches("altra_password", altroUtenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(altroUtenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(altroUtenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(altroUtenteAggiornato.getEmail()).isNotEqualTo("email_aggiornata@example.com");
		Assertions.assertThat(altroUtenteAggiornato.getEmail()).isEqualTo("altro@example.com");
		Assertions.assertThat(altroUtenteAggiornato.getIndirizzo()).isBlank();
	}

	/**
	 * Creo un utente e gli faccio aggiornare il suo indirizzo basandomi sul login,
	 * mi aspetto funzioni
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateAddressTestRecognizingByLogin() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").indirizzo("Via vecchia 1").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		UtenteReq updateRequest = UtenteReq.builder().indirizzo("Via nuova 2").build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// recupero direttamente l'utente da repository
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isEqualTo("Via nuova 2");
	}

	/**
	 * Creo un utente e gli faccio aggiornare l'indirizzo basandomi sull'id utente
	 * ma dovrebbe essere lo stesso utente loggato, mi aspetto che funzioni
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateAddressTestRecognizingById() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").indirizzo("Via vecchia 1").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		// recupero i dati utente con il webservice me
		MvcResult mvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isOk()).andReturn();

		String responseStringMe = mvcResultMe.getResponse().getContentAsString();

		UtenteDTO utenteDTO = objectMapper.readValue(responseStringMe, UtenteDTO.class);

		UtenteReq updateRequest = UtenteReq.builder().indirizzo("Via nuova 2").id(utenteDTO.getId()).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// recupero direttamente l'utente da repository
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isEqualTo("Via nuova 2");
	}

	/**
	 * Creo due utenti senza privilegi amministrativi e provo ad aggiornare
	 * l'indirizzo dell'altro basandomi sull'id utente, mi aspetto vada in errore
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateAddressTestRecognizingByIdWrongId() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").indirizzo("Via vecchia 1").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		String altroUtenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("altro")
				.password("altra_password").email("altro@example.com").indirizzo("Via vecchia altra 1").build());
		mockMvc.perform(
				post("/rest/utente/public/create").content(altroUtenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login dell'altro utente

		String altroLoginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("altro").password("altra_password").build());

		MvcResult altroMvcResult = mockMvc
				.perform(post("/rest/auth/login").content(altroLoginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String altroResponseString = altroMvcResult.getResponse().getContentAsString();

		LoginDTO altroLoginDTO = objectMapper.readValue(altroResponseString, LoginDTO.class);

		Assertions.assertThat(altroLoginDTO.getAccessToken()).isNotBlank();
		Assertions.assertThat(altroLoginDTO.getTokenType()).isEqualTo("Bearer");

		String altroAccessToken = altroLoginDTO.getAccessToken();

		// recupero i dati utente con il webservice me
		MvcResult altroMvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + altroAccessToken))
				.andExpect(status().isOk()).andReturn();

		String altroResponseStringMe = altroMvcResultMe.getResponse().getContentAsString();

		UtenteDTO altroUtenteDTO = objectMapper.readValue(altroResponseStringMe, UtenteDTO.class);

		// faccio la richiesta usando come id quello dell'altro utente
		UtenteReq updateRequest = UtenteReq.builder().indirizzo("Via nuova altra 2").id(altroUtenteDTO.getId()).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		// come token di autenticazione uso quello di utente
		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

		// recupero direttamente l' utente da repository mi aspetto non sia cambiato
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isNotEqualTo("Via nuova altra 2");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isEqualTo("Via vecchia 1");

		// recupero direttamente l'altro utente da repository mi aspetto non sia
		// cambiato
		Utente altroUtenteAggiornato = utenteRepository.findByUsername("altro")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(altroUtenteAggiornato.getUsername()).isEqualTo("altro");
		Assertions.assertThat(passwordEncoder.matches("altra_password", altroUtenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(altroUtenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(altroUtenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(altroUtenteAggiornato.getEmail()).isEqualTo("altro@example.com");
		Assertions.assertThat(altroUtenteAggiornato.getIndirizzo()).isNotEqualTo("Via nuova altra 2");
		Assertions.assertThat(altroUtenteAggiornato.getIndirizzo()).isEqualTo("Via vecchia altra 1");
	}


	/**
	 * Creo un utente senza indirizzo e gli faccio aggiungere il suo indirizzo
	 * basandomi sul login, mi aspetto funzioni
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateAddressWithoutAddressTestRecognizingByLogin() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(
				UtenteDTO.builder().username("utente").password("password").email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		UtenteReq updateRequest = UtenteReq.builder().indirizzo("Via nuova 2").build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// recupero direttamente l'utente da repository
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isEqualTo("Via nuova 2");
	}

	/**
	 * Creo un utente senza indirizzo e gli faccio aggiornare l'indirizzo basandomi
	 * sull'id utente ma dovrebbe essere lo stesso utente loggato, mi aspetto che
	 * funzioni
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateAddressWithoutAddressTestRecognizingById() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(
				UtenteDTO.builder().username("utente").password("password").email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		// recupero i dati utente con il webservice me
		MvcResult mvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isOk()).andReturn();

		String responseStringMe = mvcResultMe.getResponse().getContentAsString();

		UtenteDTO utenteDTO = objectMapper.readValue(responseStringMe, UtenteDTO.class);

		UtenteReq updateRequest = UtenteReq.builder().indirizzo("Via nuova 2").id(utenteDTO.getId()).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// recupero direttamente l'utente da repository
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isEqualTo("Via nuova 2");
	}

	/**
	 * Creo due utenti senza privilegi amministrativi e senza indirizzi e provo ad
	 * aggiornare l'indirizzo dell'altro basandomi sull'id utente, mi aspetto vada
	 * in errore
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateAddressWithoutAddressTestRecognizingByIdWrongId() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(
				UtenteDTO.builder().username("utente").password("password").email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		String altroUtenteJSON = objectMapper.writeValueAsString(
				UtenteDTO.builder().username("altro").password("altra_password").email("altro@example.com").build());
		mockMvc.perform(
				post("/rest/utente/public/create").content(altroUtenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login dell'altro utente

		String altroLoginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("altro").password("altra_password").build());

		MvcResult altroMvcResult = mockMvc
				.perform(post("/rest/auth/login").content(altroLoginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String altroResponseString = altroMvcResult.getResponse().getContentAsString();

		LoginDTO altroLoginDTO = objectMapper.readValue(altroResponseString, LoginDTO.class);

		Assertions.assertThat(altroLoginDTO.getAccessToken()).isNotBlank();
		Assertions.assertThat(altroLoginDTO.getTokenType()).isEqualTo("Bearer");

		String altroAccessToken = altroLoginDTO.getAccessToken();

		// recupero i dati utente con il webservice me
		MvcResult altroMvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + altroAccessToken))
				.andExpect(status().isOk()).andReturn();

		String altroResponseStringMe = altroMvcResultMe.getResponse().getContentAsString();

		UtenteDTO altroUtenteDTO = objectMapper.readValue(altroResponseStringMe, UtenteDTO.class);

		// faccio la richiesta usando come id quello dell'altro utente
		UtenteReq updateRequest = UtenteReq.builder().indirizzo("Via nuova altra 2").id(altroUtenteDTO.getId()).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		// come token di autenticazione uso quello di utente
		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

		// recupero direttamente l' utente da repository mi aspetto non sia cambiato
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isNotEqualTo("Via nuova altra 2");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();

		// recupero direttamente l'altro utente da repository mi aspetto non sia
		// cambiato
		Utente altroUtenteAggiornato = utenteRepository.findByUsername("altro")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(altroUtenteAggiornato.getUsername()).isEqualTo("altro");
		Assertions.assertThat(passwordEncoder.matches("altra_password", altroUtenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(altroUtenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(altroUtenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(altroUtenteAggiornato.getEmail()).isEqualTo("altro@example.com");
		Assertions.assertThat(altroUtenteAggiornato.getIndirizzo()).isNotEqualTo("Via nuova altra 2");
		Assertions.assertThat(altroUtenteAggiornato.getIndirizzo()).isBlank();
	}

	/**
	 * Creo un utente e gli faccio aggiornare la password basandomi sul login, mi
	 * aspetto funzioni
	 * 
	 * @throws Exception
	 */
	@Test
	public void updatePasswordTestRecognizingByLogin() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		UtenteReq updateRequest = UtenteReq.builder().password("password_nuova").build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		//recupero direttamente l'utente da repository
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password_nuova", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();
	}

	/**
	 * Creo un utente e gli faccio aggiornare la password basandomi sull'id utente
	 * ma dovrebbe essere lo stesso utente loggato, mi aspetto che funzioni
	 * 
	 * @throws Exception
	 */
	@Test
	public void updatePasswordTestRecognizingById() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(
				UtenteDTO.builder().username("utente").password("password").email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		//recupero i dati utente con il webservice me
		MvcResult mvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isOk()).andReturn();

		String responseStringMe = mvcResultMe.getResponse().getContentAsString();

		UtenteDTO utenteDTO = objectMapper.readValue(responseStringMe, UtenteDTO.class);

		UtenteReq updateRequest = UtenteReq.builder().password("password_nuova").id(utenteDTO.getId()).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		//recupero direttamente l'utente da repository
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password_nuova", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();
	}

	/**
	 * Creo due utenti senza privilegi amministrativi e provo ad aggiornare la
	 * password dell'altro basandomi sull'id utente, mi aspetto vada in errore
	 * 
	 * @throws Exception
	 */
	@Test
	public void updatePasswordTestRecognizingByIdWrongId() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(
				UtenteDTO.builder().username("utente").password("password").email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		String altroUtenteJSON = objectMapper.writeValueAsString(
				UtenteDTO.builder().username("altro").password("altra_password").email("altro@example.com").build());
		mockMvc.perform(
				post("/rest/utente/public/create").content(altroUtenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login dell'altro utente

		String altroLoginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("altro").password("altra_password").build());

		MvcResult altroMvcResult = mockMvc
				.perform(post("/rest/auth/login").content(altroLoginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String altroResponseString = altroMvcResult.getResponse().getContentAsString();

		LoginDTO altroLoginDTO = objectMapper.readValue(altroResponseString, LoginDTO.class);

		Assertions.assertThat(altroLoginDTO.getAccessToken()).isNotBlank();
		Assertions.assertThat(altroLoginDTO.getTokenType()).isEqualTo("Bearer");

		String altroAccessToken = altroLoginDTO.getAccessToken();

		//recupero i dati utente con il webservice me
		MvcResult altroMvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + altroAccessToken))
				.andExpect(status().isOk()).andReturn();

		String altroResponseStringMe = altroMvcResultMe.getResponse().getContentAsString();

		UtenteDTO altroUtenteDTO = objectMapper.readValue(altroResponseStringMe, UtenteDTO.class);

		//faccio la richiesta usando come id quello dell'altro utente
		UtenteReq updateRequest = UtenteReq.builder().password("altra_nuova_password").id(altroUtenteDTO.getId())
				.build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		//come token di autenticazione uso quello di utente
		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

		//recupero direttamente l' utente da repository  mi aspetto non sia cambiato
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(passwordEncoder.matches("altra_nuova_password", utenteAggiornato.getPassword()))
				.isFalse();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();

		// recupero direttamente l'altro utente da repository mi aspetto non sia
		// cambiato
		Utente altroUtenteAggiornato = utenteRepository.findByUsername("altro")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(altroUtenteAggiornato.getUsername()).isEqualTo("altro");
		Assertions.assertThat(passwordEncoder.matches("altra_password", altroUtenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(passwordEncoder.matches("altra_nuova_password", altroUtenteAggiornato.getPassword()))
				.isFalse();
		Assertions.assertThat(altroUtenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(altroUtenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(altroUtenteAggiornato.getEmail()).isEqualTo("altro@example.com");
		Assertions.assertThat(altroUtenteAggiornato.getIndirizzo()).isBlank();
	}
	
	
	/**
	 * Creo un utente e gli faccio aggiornare l'username basandomi sul login,
	 * mi aspetto che fallisca
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateUsernameTestRecognizingByLogin() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		UtenteReq updateRequest = UtenteReq.builder().username("nuovo_username").build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		
		// provo a recuperare l'utente con il nuovo username
		Optional<Utente> utenteAggiornatoNuovoUsername = utenteRepository.findByUsername("nuovo_username");
		Assertions.assertThat(utenteAggiornatoNuovoUsername).isEmpty();
		
		
		// recupero direttamente l'utente da repository
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isNotEqualTo("nuovo_username");
		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();
	}

	/**
	 * Creo un utente e gli faccio aggiornare lo username basandomi sull'id utente
	 * ma dovrebbe essere lo stesso utente loggato, mi aspetto che fallisca
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateUsernameTestRecognizingById() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		// recupero i dati utente con il webservice me
		MvcResult mvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isOk()).andReturn();

		String responseStringMe = mvcResultMe.getResponse().getContentAsString();

		UtenteDTO utenteDTO = objectMapper.readValue(responseStringMe, UtenteDTO.class);

		UtenteReq updateRequest = UtenteReq.builder().username("nuovo_username").id(utenteDTO.getId()).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		// recupero direttamente l'utente da repository
		Utente utenteAggiornato = utenteRepository.findById(utenteDTO.getId())
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isNotEqualTo("nuovo_username");
		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();
	}

	/**
	 * Creo due utenti senza privilegi amministrativi e provo ad aggiornare
	 * l'indirizzo dell'altro basandomi sull'id utente, mi aspetto vada in errore
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateUsernameTestRecognizingByIdWrongId() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		String altroUtenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("altro")
				.password("altra_password").email("altro@example.com").build());
		mockMvc.perform(
				post("/rest/utente/public/create").content(altroUtenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login dell'altro utente

		String altroLoginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("altro").password("altra_password").build());

		MvcResult altroMvcResult = mockMvc
				.perform(post("/rest/auth/login").content(altroLoginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String altroResponseString = altroMvcResult.getResponse().getContentAsString();

		LoginDTO altroLoginDTO = objectMapper.readValue(altroResponseString, LoginDTO.class);

		Assertions.assertThat(altroLoginDTO.getAccessToken()).isNotBlank();
		Assertions.assertThat(altroLoginDTO.getTokenType()).isEqualTo("Bearer");

		String altroAccessToken = altroLoginDTO.getAccessToken();

		// recupero i dati utente con il webservice me
		MvcResult altroMvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + altroAccessToken))
				.andExpect(status().isOk()).andReturn();

		String altroResponseStringMe = altroMvcResultMe.getResponse().getContentAsString();

		UtenteDTO altroUtenteDTO = objectMapper.readValue(altroResponseStringMe, UtenteDTO.class);

		// faccio la richiesta usando come id quello dell'altro utente
		UtenteReq updateRequest = UtenteReq.builder().username("nuovo_username").id(altroUtenteDTO.getId()).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		// come token di autenticazione uso quello di utente
		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
		
		
		//verifico che non ci sia un utente con il nuovo username
		Assertions.assertThat(utenteRepository.existsByUsername("nuovo_username")).isFalse();

		// recupero direttamente l' utente da repository mi aspetto non sia cambiato
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(utenteAggiornato.getUsername()).isNotEqualTo("nuovo_username");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();

		// recupero direttamente l'altro utente da repository mi aspetto non sia
		// cambiato
		Utente altroUtenteAggiornato = utenteRepository.findById(altroUtenteDTO.getId())
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(altroUtenteAggiornato.getUsername()).isEqualTo("altro");
		Assertions.assertThat(altroUtenteAggiornato.getUsername()).isNotEqualTo("nuovo_username");
		Assertions.assertThat(passwordEncoder.matches("altra_password", altroUtenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(altroUtenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(altroUtenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(altroUtenteAggiornato.getEmail()).isEqualTo("altro@example.com");
		Assertions.assertThat(altroUtenteAggiornato.getIndirizzo()).isBlank();
	}
	
	/**
	 * Creo un utente e gli faccio aggiornare il suo ruolo ad {@link Ruolo#UTENTE} basandomi sul login,
	 * mi aspetto funzioni, anche perché in realtà non cambia
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateRoleToUserTestRecognizingByLogin() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		UtenteReq updateRequest = UtenteReq.builder().ruolo(Ruolo.UTENTE).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// recupero direttamente l'utente da repository
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();
	}

	/**
	 * Creo un utente e gli faccio aggiornare il suo ruolo ad {@link Ruolo#UTENTE} basandomi sull'id utente
	 * ma dovrebbe essere lo stesso utente loggato, mi aspetto che funzioni anche perché il ruolo non cambia
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateRoleToUserTestRecognizingById() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		// recupero i dati utente con il webservice me
		MvcResult mvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isOk()).andReturn();

		String responseStringMe = mvcResultMe.getResponse().getContentAsString();

		UtenteDTO utenteDTO = objectMapper.readValue(responseStringMe, UtenteDTO.class);

		UtenteReq updateRequest = UtenteReq.builder().ruolo(Ruolo.UTENTE).id(utenteDTO.getId()).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// recupero direttamente l'utente da repository
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();
	}

	/**
	 * Creo due utenti senza privilegi amministrativi e provo ad aggiornare
	 * il ruolo dell'altro ad {@link Ruolo#UTENTE} basandomi sull'id utente, mi aspetto vada in errore perché non consentito di cambiare
	 * il ruolo a qualcun altro, ma essendo comunque sempre ruolo utente, non cambia
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateRoleToUserTestRecognizingByIdWrongId() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		String altroUtenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("altro")
				.password("altra_password").email("altro@example.com").build());
		mockMvc.perform(
				post("/rest/utente/public/create").content(altroUtenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login dell'altro utente

		String altroLoginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("altro").password("altra_password").build());

		MvcResult altroMvcResult = mockMvc
				.perform(post("/rest/auth/login").content(altroLoginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String altroResponseString = altroMvcResult.getResponse().getContentAsString();

		LoginDTO altroLoginDTO = objectMapper.readValue(altroResponseString, LoginDTO.class);

		Assertions.assertThat(altroLoginDTO.getAccessToken()).isNotBlank();
		Assertions.assertThat(altroLoginDTO.getTokenType()).isEqualTo("Bearer");

		String altroAccessToken = altroLoginDTO.getAccessToken();

		// recupero i dati utente con il webservice me
		MvcResult altroMvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + altroAccessToken))
				.andExpect(status().isOk()).andReturn();

		String altroResponseStringMe = altroMvcResultMe.getResponse().getContentAsString();

		UtenteDTO altroUtenteDTO = objectMapper.readValue(altroResponseStringMe, UtenteDTO.class);

		// faccio la richiesta usando come id quello dell'altro utente
		UtenteReq updateRequest = UtenteReq.builder().ruolo(Ruolo.UTENTE).id(altroUtenteDTO.getId()).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		// come token di autenticazione uso quello di utente
		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

		// recupero direttamente l' utente da repository mi aspetto non sia cambiato
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();

		// recupero direttamente l'altro utente da repository mi aspetto non sia
		// cambiato
		Utente altroUtenteAggiornato = utenteRepository.findByUsername("altro")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(altroUtenteAggiornato.getUsername()).isEqualTo("altro");
		Assertions.assertThat(passwordEncoder.matches("altra_password", altroUtenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(altroUtenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(altroUtenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(altroUtenteAggiornato.getEmail()).isEqualTo("altro@example.com");
		Assertions.assertThat(altroUtenteAggiornato.getIndirizzo()).isBlank();
	}

	
	/**
	 * Creo un utente e gli faccio aggiornare il suo ruolo ad {@link Ruolo#ADMIN} basandomi sul login,
	 * mi aspetto fallisca, perché un utente non può cambiarsi il ruolo in amministratore
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateRoleToAdminTestRecognizingByLogin() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		UtenteReq updateRequest = UtenteReq.builder().ruolo(Ruolo.ADMIN).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// recupero direttamente l'utente da repository
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isNotEqualTo(Ruolo.ADMIN);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();
	}

	/**
	 * Creo un utente e gli faccio aggiornare il suo ruolo ad {@link Ruolo#ADMIN} basandomi sull'id utente
	 * ma dovrebbe essere lo stesso utente loggato, mi aspetto che fallisca perché un utente non può cambarsi ruolo in amministratore
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateRoleToAdminTestRecognizingById() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		// recupero i dati utente con il webservice me
		MvcResult mvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isOk()).andReturn();

		String responseStringMe = mvcResultMe.getResponse().getContentAsString();

		UtenteDTO utenteDTO = objectMapper.readValue(responseStringMe, UtenteDTO.class);

		UtenteReq updateRequest = UtenteReq.builder().ruolo(Ruolo.ADMIN).id(utenteDTO.getId()).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// recupero direttamente l'utente da repository
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isNotEqualTo(Ruolo.ADMIN);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();
	}

	/**
	 * Creo due utenti senza privilegi amministrativi e provo ad aggiornare
	 * il ruolo dell'altro ad {@link Ruolo#ADMIN} basandomi sull'id utente, mi aspetto vada in errore perché non consentito di cambiare
	 * il ruolo a qualcun altro, e che i ruoli rimangano sempre utente
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateRoleToAdminTestRecognizingByIdWrongId() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		String altroUtenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("altro")
				.password("altra_password").email("altro@example.com").build());
		mockMvc.perform(
				post("/rest/utente/public/create").content(altroUtenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login dell'altro utente

		String altroLoginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("altro").password("altra_password").build());

		MvcResult altroMvcResult = mockMvc
				.perform(post("/rest/auth/login").content(altroLoginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String altroResponseString = altroMvcResult.getResponse().getContentAsString();

		LoginDTO altroLoginDTO = objectMapper.readValue(altroResponseString, LoginDTO.class);

		Assertions.assertThat(altroLoginDTO.getAccessToken()).isNotBlank();
		Assertions.assertThat(altroLoginDTO.getTokenType()).isEqualTo("Bearer");

		String altroAccessToken = altroLoginDTO.getAccessToken();

		// recupero i dati utente con il webservice me
		MvcResult altroMvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + altroAccessToken))
				.andExpect(status().isOk()).andReturn();

		String altroResponseStringMe = altroMvcResultMe.getResponse().getContentAsString();

		UtenteDTO altroUtenteDTO = objectMapper.readValue(altroResponseStringMe, UtenteDTO.class);

		// faccio la richiesta usando come id quello dell'altro utente
		UtenteReq updateRequest = UtenteReq.builder().ruolo(Ruolo.ADMIN).id(altroUtenteDTO.getId()).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		// come token di autenticazione uso quello di utente
		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

		// recupero direttamente l' utente da repository mi aspetto non sia cambiato
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isNotEqualTo(Ruolo.ADMIN);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();

		// recupero direttamente l'altro utente da repository mi aspetto non sia
		// cambiato
		Utente altroUtenteAggiornato = utenteRepository.findByUsername("altro")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(altroUtenteAggiornato.getUsername()).isEqualTo("altro");
		Assertions.assertThat(passwordEncoder.matches("altra_password", altroUtenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(altroUtenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(altroUtenteAggiornato.getRuolo()).isNotEqualTo(Ruolo.ADMIN);
		Assertions.assertThat(altroUtenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(altroUtenteAggiornato.getEmail()).isEqualTo("altro@example.com");
		Assertions.assertThat(altroUtenteAggiornato.getIndirizzo()).isBlank();
	}
	
	
	/**
	 * Creo un utente e gli faccio aggiornare il suo credito basandomi sul login,
	 * mi aspetto fallisca
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateCreditTestRecognizingByLogin() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		UtenteReq updateRequest = UtenteReq.builder().credito(BigDecimal.valueOf(1000)).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// recupero direttamente l'utente da repository
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isNotEqualTo(BigDecimal.valueOf(1000));
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();
	}

	/**
	 * Creo un utente e gli faccio aggiornare il suo credito basandomi sull'id utente
	 * ma dovrebbe essere lo stesso utente loggato, mi aspetto che fallisca
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateCreditTestRecognizingById() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		// recupero i dati utente con il webservice me
		MvcResult mvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isOk()).andReturn();

		String responseStringMe = mvcResultMe.getResponse().getContentAsString();

		UtenteDTO utenteDTO = objectMapper.readValue(responseStringMe, UtenteDTO.class);

		UtenteReq updateRequest = UtenteReq.builder().credito(BigDecimal.valueOf(1000)).id(utenteDTO.getId()).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// recupero direttamente l'utente da repository
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isNotEqualTo(BigDecimal.valueOf(1000));
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();
	}

	/**
	 * Creo due utenti senza privilegi amministrativi e provo ad aggiornare
	 * il credito dell'altro basandomi sull'id utente, mi aspetto vada in errore
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateCreditTestRecognizingByIdWrongId() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		String altroUtenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("altro")
				.password("altra_password").email("altro@example.com").build());
		mockMvc.perform(
				post("/rest/utente/public/create").content(altroUtenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login dell'altro utente

		String altroLoginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("altro").password("altra_password").build());

		MvcResult altroMvcResult = mockMvc
				.perform(post("/rest/auth/login").content(altroLoginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String altroResponseString = altroMvcResult.getResponse().getContentAsString();

		LoginDTO altroLoginDTO = objectMapper.readValue(altroResponseString, LoginDTO.class);

		Assertions.assertThat(altroLoginDTO.getAccessToken()).isNotBlank();
		Assertions.assertThat(altroLoginDTO.getTokenType()).isEqualTo("Bearer");

		String altroAccessToken = altroLoginDTO.getAccessToken();

		// recupero i dati utente con il webservice me
		MvcResult altroMvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + altroAccessToken))
				.andExpect(status().isOk()).andReturn();

		String altroResponseStringMe = altroMvcResultMe.getResponse().getContentAsString();

		UtenteDTO altroUtenteDTO = objectMapper.readValue(altroResponseStringMe, UtenteDTO.class);

		// faccio la richiesta usando come id quello dell'altro utente
		UtenteReq updateRequest = UtenteReq.builder().credito(BigDecimal.valueOf(1000)).id(altroUtenteDTO.getId()).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		// come token di autenticazione uso quello di utente
		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

		// recupero direttamente l' utente da repository mi aspetto non sia cambiato
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isNotEqualTo(BigDecimal.valueOf(1000));
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();

		// recupero direttamente l'altro utente da repository mi aspetto non sia
		// cambiato
		Utente altroUtenteAggiornato = utenteRepository.findByUsername("altro")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(altroUtenteAggiornato.getUsername()).isEqualTo("altro");
		Assertions.assertThat(passwordEncoder.matches("altra_password", altroUtenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(altroUtenteAggiornato.getCredito()).isNotEqualTo(BigDecimal.valueOf(1000));
		Assertions.assertThat(altroUtenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(altroUtenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(altroUtenteAggiornato.getEmail()).isEqualTo("altro@example.com");
		Assertions.assertThat(altroUtenteAggiornato.getIndirizzo()).isBlank();
	}
	
	
	/**
	 * Creo un utente amministratore e gli faccio aggiornare la sua email basandomi sul login, mi
	 * aspetto funzioni
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateEmailTestRecognizingByLoginByAdmin() throws Exception {
		
		utenteService.create(UtenteReq.builder().username("admin").password("admin").ruolo(Ruolo.ADMIN).email("admin@example.com").build());

		// faccio il login

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

		UtenteReq updateRequest = UtenteReq.builder().email("email_aggiornata@example.com").build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/admin/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// recupero direttamente l'utente da repository
		Utente utenteAggiornato = utenteRepository.findByUsername("admin")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("admin");
		Assertions.assertThat(passwordEncoder.matches("admin", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.ADMIN);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("email_aggiornata@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();
	}

	/**
	 * Creo un utente amministratore e gli faccio aggiornare la sua email basandomi sull'id utente
	 * ma dovrebbe essere lo stesso utente loggato, mi aspetto che funzioni
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateEmailTestRecognizingByIdByAdmin() throws Exception {
		
		utenteService.create(UtenteReq.builder().username("admin").password("admin").ruolo(Ruolo.ADMIN).email("admin@example.com").build());


		// faccio il login

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

		// recupero i dati utente con il webservice me
		MvcResult mvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isOk()).andReturn();

		String responseStringMe = mvcResultMe.getResponse().getContentAsString();

		UtenteDTO utenteDTO = objectMapper.readValue(responseStringMe, UtenteDTO.class);

		UtenteReq updateRequest = UtenteReq.builder().email("email_aggiornata@example.com").id(utenteDTO.getId())
				.build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/admin/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// recupero direttamente l'utente da repository
		Utente utenteAggiornato = utenteRepository.findByUsername("admin")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("admin");
		Assertions.assertThat(passwordEncoder.matches("admin", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.ADMIN);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("email_aggiornata@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();
	}

	/**
	 * Creo un utente amministratore ed uno senza privilegi amministrativi e faccio aggiornare l'email 
	 * dall'amministratore usando l'id utente dell'utente non aministratore, mi aspetto aggiorni
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateEmailTestRecognizingOtherByIdByAdmin() throws Exception {

		utenteService.create(UtenteReq.builder().username("admin").password("admin").ruolo(Ruolo.ADMIN).email("admin@example.com").build());

		// faccio il login

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

		String altroUtenteJSON = objectMapper.writeValueAsString(
				UtenteDTO.builder().username("altro").password("altra_password").email("altro@example.com").build());
		mockMvc.perform(
				post("/rest/utente/public/create").content(altroUtenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login dell'altro utente

		String altroLoginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("altro").password("altra_password").build());

		MvcResult altroMvcResult = mockMvc
				.perform(post("/rest/auth/login").content(altroLoginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String altroResponseString = altroMvcResult.getResponse().getContentAsString();

		LoginDTO altroLoginDTO = objectMapper.readValue(altroResponseString, LoginDTO.class);

		Assertions.assertThat(altroLoginDTO.getAccessToken()).isNotBlank();
		Assertions.assertThat(altroLoginDTO.getTokenType()).isEqualTo("Bearer");

		String altroAccessToken = altroLoginDTO.getAccessToken();

		// recupero i dati utente con il webservice me
		MvcResult altroMvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + altroAccessToken))
				.andExpect(status().isOk()).andReturn();

		String altroResponseStringMe = altroMvcResultMe.getResponse().getContentAsString();

		UtenteDTO altroUtenteDTO = objectMapper.readValue(altroResponseStringMe, UtenteDTO.class);

		// faccio la richiesta usando come id quello dell'altro utente
		UtenteReq updateRequest = UtenteReq.builder().email("email_aggiornata@example.com").id(altroUtenteDTO.getId())
				.build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		// come token di autenticazione uso quello di admin
		mockMvc.perform(patch("/rest/utente/admin/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// recupero direttamente l' utente da repository mi aspetto non sia cambiato
		Utente adminAggiornato = utenteRepository.findByUsername("admin")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(adminAggiornato.getUsername()).isEqualTo("admin");
		Assertions.assertThat(passwordEncoder.matches("admin", adminAggiornato.getPassword())).isTrue();
		Assertions.assertThat(adminAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(adminAggiornato.getRuolo()).isEqualTo(Ruolo.ADMIN);
		Assertions.assertThat(adminAggiornato.getEmail()).isNotEqualTo("email_aggiornata@example.com");//verifico che non sia stata cambiata l'email dell'amministratore
		Assertions.assertThat(adminAggiornato.getEmail()).isEqualTo("admin@example.com");
		Assertions.assertThat(adminAggiornato.getIndirizzo()).isBlank();

		// recupero direttamente l'altro utente da repository mi aspetto non sia
		// cambiato
		Utente altroUtenteAggiornato = utenteRepository.findByUsername("altro")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(altroUtenteAggiornato.getUsername()).isEqualTo("altro");
		Assertions.assertThat(passwordEncoder.matches("altra_password", altroUtenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(altroUtenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(altroUtenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(altroUtenteAggiornato.getEmail()).isNotEqualTo("altro@example.com");
		Assertions.assertThat(altroUtenteAggiornato.getEmail()).isEqualTo("email_aggiornata@example.com");
		Assertions.assertThat(altroUtenteAggiornato.getIndirizzo()).isBlank();
	}

	/**
	 * Creo un utente amministratore e gli faccio aggiornare il suo indirizzo basandomi sul login,
	 * mi aspetto funzioni
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateAddressTestRecognizingByLoginByAdmin() throws Exception {

		utenteService.create(UtenteReq.builder().username("admin").password("admin").ruolo(Ruolo.ADMIN).indirizzo("Via vecchia 1").email("admin@example.com").build());

		// faccio il login

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

		UtenteReq updateRequest = UtenteReq.builder().indirizzo("Via nuova 2").build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/admin/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// recupero direttamente l'utente da repository
		Utente utenteAggiornato = utenteRepository.findByUsername("admin")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("admin");
		Assertions.assertThat(passwordEncoder.matches("admin", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.ADMIN);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("admin@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isEqualTo("Via nuova 2");
	}

	/**
	 * Creo un utente amministratore e gli faccio aggiornare l'indirizzo basandomi sull'id utente
	 * ma dovrebbe essere lo stesso utente loggato, mi aspetto che funzioni
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateAddressTestRecognizingByIdByAdmin() throws Exception {

		utenteService.create(UtenteReq.builder().username("admin").password("admin").ruolo(Ruolo.ADMIN).indirizzo("Via vecchia 1").email("admin@example.com").build());

		// faccio il login

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

		// recupero i dati utente con il webservice me
		MvcResult mvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isOk()).andReturn();

		String responseStringMe = mvcResultMe.getResponse().getContentAsString();

		UtenteDTO utenteDTO = objectMapper.readValue(responseStringMe, UtenteDTO.class);

		UtenteReq updateRequest = UtenteReq.builder().indirizzo("Via nuova 2").id(utenteDTO.getId()).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/admin/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// recupero direttamente l'utente da repository
		Utente utenteAggiornato = utenteRepository.findByUsername("admin")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("admin");
		Assertions.assertThat(passwordEncoder.matches("admin", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.ADMIN);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("admin@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isEqualTo("Via nuova 2");
	}

	/**
	 * Creo un utente amministratore ed uno senza privilegi amministrativi e faccio aggiornare
	 * l'indirizzo dall'amministratore usando l'id utente dell'utente non aministratore, mi aspetto aggiorni
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateAddressTestRecognizingRecognizingOtherByIdByAdmin() throws Exception {

		utenteService.create(UtenteReq.builder().username("admin").password("admin").ruolo(Ruolo.ADMIN).indirizzo("Via vecchia 1").email("admin@example.com").build());

		// faccio il login

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

		String altroUtenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("altro")
				.password("altra_password").email("altro@example.com").indirizzo("Via vecchia altra 1").build());
		mockMvc.perform(
				post("/rest/utente/public/create").content(altroUtenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login dell'altro utente

		String altroLoginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("altro").password("altra_password").build());

		MvcResult altroMvcResult = mockMvc
				.perform(post("/rest/auth/login").content(altroLoginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String altroResponseString = altroMvcResult.getResponse().getContentAsString();

		LoginDTO altroLoginDTO = objectMapper.readValue(altroResponseString, LoginDTO.class);

		Assertions.assertThat(altroLoginDTO.getAccessToken()).isNotBlank();
		Assertions.assertThat(altroLoginDTO.getTokenType()).isEqualTo("Bearer");

		String altroAccessToken = altroLoginDTO.getAccessToken();

		// recupero i dati utente con il webservice me
		MvcResult altroMvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + altroAccessToken))
				.andExpect(status().isOk()).andReturn();

		String altroResponseStringMe = altroMvcResultMe.getResponse().getContentAsString();

		UtenteDTO altroUtenteDTO = objectMapper.readValue(altroResponseStringMe, UtenteDTO.class);

		// faccio la richiesta usando come id quello dell'altro utente
		UtenteReq updateRequest = UtenteReq.builder().indirizzo("Via nuova altra 2").id(altroUtenteDTO.getId()).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		// come token di autenticazione uso quello di admin
		mockMvc.perform(patch("/rest/utente/admin/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// recupero direttamente l'admin da repository mi aspetto non sia cambiato
		Utente adminAggiornato = utenteRepository.findByUsername("admin")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(adminAggiornato.getUsername()).isEqualTo("admin");
		Assertions.assertThat(passwordEncoder.matches("admin", adminAggiornato.getPassword())).isTrue();
		Assertions.assertThat(adminAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(adminAggiornato.getRuolo()).isEqualTo(Ruolo.ADMIN);
		Assertions.assertThat(adminAggiornato.getEmail()).isEqualTo("admin@example.com");
		Assertions.assertThat(adminAggiornato.getIndirizzo()).isNotEqualTo("Via nuova altra 2");
		Assertions.assertThat(adminAggiornato.getIndirizzo()).isEqualTo("Via vecchia 1");

		// recupero direttamente l'altro utente da repository mi aspetto sia
		// cambiato
		Utente altroUtenteAggiornato = utenteRepository.findByUsername("altro")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(altroUtenteAggiornato.getUsername()).isEqualTo("altro");
		Assertions.assertThat(passwordEncoder.matches("altra_password", altroUtenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(altroUtenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(altroUtenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(altroUtenteAggiornato.getEmail()).isEqualTo("altro@example.com");
		Assertions.assertThat(altroUtenteAggiornato.getIndirizzo()).isNotEqualTo("Via vecchia altra 1");
		Assertions.assertThat(altroUtenteAggiornato.getIndirizzo()).isEqualTo("Via nuova altra 2");
		
	}


	/**
	 * Creo un utente amministratore senza indirizzo e gli faccio aggiungere il suo indirizzo
	 * basandomi sul login, mi aspetto funzioni
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateAddressWithoutAddressTestRecognizingByLoginByAdmin() throws Exception {

		utenteService.create(UtenteReq.builder().username("admin").password("admin").ruolo(Ruolo.ADMIN).email("admin@example.com").build());

		// faccio il login

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

		UtenteReq updateRequest = UtenteReq.builder().indirizzo("Via nuova 2").build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/admin/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// recupero direttamente l'utente da repository
		Utente utenteAggiornato = utenteRepository.findByUsername("admin")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("admin");
		Assertions.assertThat(passwordEncoder.matches("admin", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.ADMIN);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("admin@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isEqualTo("Via nuova 2");
	}

	/**
	 * Creo un utente amministratore senza indirizzo e gli faccio aggiornare l'indirizzo basandomi
	 * sull'id utente ma dovrebbe essere lo stesso utente loggato, mi aspetto che
	 * funzioni
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateAddressWithoutAddressTestRecognizingByIdByAdmin() throws Exception {

		utenteService.create(UtenteReq.builder().username("admin").password("admin").ruolo(Ruolo.ADMIN).email("admin@example.com").build());

		// faccio il login

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

		// recupero i dati utente con il webservice me
		MvcResult mvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isOk()).andReturn();

		String responseStringMe = mvcResultMe.getResponse().getContentAsString();

		UtenteDTO utenteDTO = objectMapper.readValue(responseStringMe, UtenteDTO.class);

		UtenteReq updateRequest = UtenteReq.builder().indirizzo("Via nuova 2").id(utenteDTO.getId()).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/admin/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// recupero direttamente l'utente da repository
		Utente utenteAggiornato = utenteRepository.findByUsername("admin")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("admin");
		Assertions.assertThat(passwordEncoder.matches("admin", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.ADMIN);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("admin@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isEqualTo("Via nuova 2");
	}

	/**
	 *Creo un utente amministratore ed uno senza privilegi amministrativi, entrambi senza indirizzo 
	 *e faccio aggiornare l'indirizzo dall'amministratore usando l'id utente dell'utente non aministratore, 
	 * mi aspetto aggiorni
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateAddressWithoutAddressTestRecognizingOtherByIdByAdmin() throws Exception {

		utenteService.create(UtenteReq.builder().username("admin").password("admin").ruolo(Ruolo.ADMIN).email("admin@example.com").build());

		// faccio il login

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

		String altroUtenteJSON = objectMapper.writeValueAsString(
				UtenteDTO.builder().username("altro").password("altra_password").email("altro@example.com").build());
		mockMvc.perform(
				post("/rest/utente/public/create").content(altroUtenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login dell'altro utente

		String altroLoginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("altro").password("altra_password").build());

		MvcResult altroMvcResult = mockMvc
				.perform(post("/rest/auth/login").content(altroLoginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String altroResponseString = altroMvcResult.getResponse().getContentAsString();

		LoginDTO altroLoginDTO = objectMapper.readValue(altroResponseString, LoginDTO.class);

		Assertions.assertThat(altroLoginDTO.getAccessToken()).isNotBlank();
		Assertions.assertThat(altroLoginDTO.getTokenType()).isEqualTo("Bearer");

		String altroAccessToken = altroLoginDTO.getAccessToken();

		// recupero i dati utente con il webservice me
		MvcResult altroMvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + altroAccessToken))
				.andExpect(status().isOk()).andReturn();

		String altroResponseStringMe = altroMvcResultMe.getResponse().getContentAsString();

		UtenteDTO altroUtenteDTO = objectMapper.readValue(altroResponseStringMe, UtenteDTO.class);

		// faccio la richiesta usando come id quello dell'altro utente
		UtenteReq updateRequest = UtenteReq.builder().indirizzo("Via nuova altra 2").id(altroUtenteDTO.getId()).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		// come token di autenticazione uso quello di admin
		mockMvc.perform(patch("/rest/utente/admin/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// recupero direttamente l' admin da repository mi aspetto non sia cambiato
		Utente utenteAggiornato = utenteRepository.findByUsername("admin")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("admin");
		Assertions.assertThat(passwordEncoder.matches("admin", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.ADMIN);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("admin@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isNotEqualTo("Via nuova altra 2");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();

		// recupero direttamente l'altro utente da repository mi aspetto non sia
		// cambiato
		Utente altroUtenteAggiornato = utenteRepository.findByUsername("altro")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(altroUtenteAggiornato.getUsername()).isEqualTo("altro");
		Assertions.assertThat(passwordEncoder.matches("altra_password", altroUtenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(altroUtenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(altroUtenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(altroUtenteAggiornato.getEmail()).isEqualTo("altro@example.com");
		Assertions.assertThat(altroUtenteAggiornato.getIndirizzo()).isNotBlank();
		Assertions.assertThat(altroUtenteAggiornato.getIndirizzo()).isEqualTo("Via nuova altra 2");
		
	}

	/**
	 * Creo un utente amministratore e gli faccio aggiornare la password basandomi sul login, mi
	 * aspetto funzioni
	 * 
	 * @throws Exception
	 */
	@Test
	public void updatePasswordTestRecognizingByLoginByAdmin() throws Exception {

		utenteService.create(UtenteReq.builder().username("admin").password("admin").ruolo(Ruolo.ADMIN).email("admin@example.com").build());

		// faccio il login

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

		UtenteReq updateRequest = UtenteReq.builder().password("password_nuova").build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/admin/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		//recupero direttamente admin da repository
		Utente utenteAggiornato = utenteRepository.findByUsername("admin")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("admin");
		Assertions.assertThat(passwordEncoder.matches("password_nuova", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.ADMIN);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("admin@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();
	}

	/**
	 * Creo un utente amministratore e gli faccio aggiornare la password basandomi sull'id utente
	 * ma dovrebbe essere lo stesso utente loggato, mi aspetto che funzioni
	 * 
	 * @throws Exception
	 */
	@Test
	public void updatePasswordTestRecognizingByIdByAdmin() throws Exception {

		utenteService.create(UtenteReq.builder().username("admin").password("admin").ruolo(Ruolo.ADMIN).email("admin@example.com").build());

		// faccio il login

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

		//recupero i dati utente con il webservice me
		MvcResult mvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isOk()).andReturn();

		String responseStringMe = mvcResultMe.getResponse().getContentAsString();

		UtenteDTO utenteDTO = objectMapper.readValue(responseStringMe, UtenteDTO.class);

		UtenteReq updateRequest = UtenteReq.builder().password("password_nuova").id(utenteDTO.getId()).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/admin/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		//recupero direttamente l'admin da repository
		Utente utenteAggiornato = utenteRepository.findByUsername("admin")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("admin");
		Assertions.assertThat(passwordEncoder.matches("password_nuova", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.ADMIN);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("admin@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();
	}

	/**
	 * Creo un utente amministratore ed uno senza privilegi amministrativi e faccio aggiornare la
	 * password dell'altro basandomi sull'id utente da parte dell'amministratore, mi aspetto che funzioni
	 * 
	 * @throws Exception
	 */
	@Test
	public void updatePasswordTestRecognizingOtherByIdByAdmin() throws Exception {

		utenteService.create(UtenteReq.builder().username("admin").password("admin").ruolo(Ruolo.ADMIN).email("admin@example.com").build());

		// faccio il login

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

		String altroUtenteJSON = objectMapper.writeValueAsString(
				UtenteDTO.builder().username("altro").password("altra_password").email("altro@example.com").build());
		mockMvc.perform(
				post("/rest/utente/public/create").content(altroUtenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login dell'altro utente

		String altroLoginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("altro").password("altra_password").build());

		MvcResult altroMvcResult = mockMvc
				.perform(post("/rest/auth/login").content(altroLoginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String altroResponseString = altroMvcResult.getResponse().getContentAsString();

		LoginDTO altroLoginDTO = objectMapper.readValue(altroResponseString, LoginDTO.class);

		Assertions.assertThat(altroLoginDTO.getAccessToken()).isNotBlank();
		Assertions.assertThat(altroLoginDTO.getTokenType()).isEqualTo("Bearer");

		String altroAccessToken = altroLoginDTO.getAccessToken();

		//recupero i dati utente con il webservice me
		MvcResult altroMvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + altroAccessToken))
				.andExpect(status().isOk()).andReturn();

		String altroResponseStringMe = altroMvcResultMe.getResponse().getContentAsString();

		UtenteDTO altroUtenteDTO = objectMapper.readValue(altroResponseStringMe, UtenteDTO.class);

		//faccio la richiesta usando come id quello dell'altro utente
		UtenteReq updateRequest = UtenteReq.builder().password("altra_nuova_password").id(altroUtenteDTO.getId())
				.build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		//come token di autenticazione uso quello di admin
		mockMvc.perform(patch("/rest/utente/admin/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		//recupero direttamente l' admin da repository  mi aspetto non sia cambiato
		Utente utenteAggiornato = utenteRepository.findByUsername("admin")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("admin");
		Assertions.assertThat(passwordEncoder.matches("admin", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(passwordEncoder.matches("altra_nuova_password", utenteAggiornato.getPassword()))
				.isFalse();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.ADMIN);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("admin@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();

		// recupero direttamente l'altro utente da repository mi aspetto sia
		// cambiato
		Utente altroUtenteAggiornato = utenteRepository.findByUsername("altro")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(altroUtenteAggiornato.getUsername()).isEqualTo("altro");
		Assertions.assertThat(passwordEncoder.matches("altra_nuova_password", altroUtenteAggiornato.getPassword()))
		.isTrue();
		Assertions.assertThat(passwordEncoder.matches("altra_password", altroUtenteAggiornato.getPassword())).isFalse();
		Assertions.assertThat(altroUtenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(altroUtenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(altroUtenteAggiornato.getEmail()).isEqualTo("altro@example.com");
		Assertions.assertThat(altroUtenteAggiornato.getIndirizzo()).isBlank();
	}
	
	
	/**
	 * Creo un utente aministratore e gli faccio aggiornare l'username basandomi sul login,
	 * mi aspetto che fallisca
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateUsernameTestRecognizingByLoginByAdmin() throws Exception {

		utenteService.create(UtenteReq.builder().username("admin").password("admin").ruolo(Ruolo.ADMIN).email("admin@example.com").build());

		// faccio il login

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

		UtenteReq updateRequest = UtenteReq.builder().username("nuovo_username_admin").build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/admin/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		
		// provo a recuperare l'admin con il nuovo username
		Optional<Utente> utenteAggiornatoNuovoUsername = utenteRepository.findByUsername("nuovo_username_admin");
		Assertions.assertThat(utenteAggiornatoNuovoUsername).isEmpty();
		
		
		// recupero direttamente l'admin da repository
		Utente utenteAggiornato = utenteRepository.findByUsername("admin")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isNotEqualTo("nuovo_username_admin");
		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("admin");
		Assertions.assertThat(passwordEncoder.matches("admin", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.ADMIN);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("admin@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();
	}

	/**
	 * Creo un utente amministratore e gli faccio aggiornare lo username basandomi sull'id utente
	 * ma dovrebbe essere lo stesso utente loggato, mi aspetto che fallisca
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateUsernameTestRecognizingByIdByAdmin() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		// recupero i dati utente con il webservice me
		MvcResult mvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isOk()).andReturn();

		String responseStringMe = mvcResultMe.getResponse().getContentAsString();

		UtenteDTO utenteDTO = objectMapper.readValue(responseStringMe, UtenteDTO.class);

		UtenteReq updateRequest = UtenteReq.builder().username("nuovo_username").id(utenteDTO.getId()).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		// recupero direttamente l'utente da repository
		Utente utenteAggiornato = utenteRepository.findById(utenteDTO.getId())
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isNotEqualTo("nuovo_username");
		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();
	}

	/*
	 * Creo due utenti senza privilegi amministrativi e provo ad aggiornare
	 * l'indirizzo dell'altro basandomi sull'id utente, mi aspetto vada in errore
	 * 
	 * @throws Exception
	 *
	@Test
	public void updateUsernameTestRecognizingByIdWrongId() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		String altroUtenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("altro")
				.password("altra_password").email("altro@example.com").build());
		mockMvc.perform(
				post("/rest/utente/public/create").content(altroUtenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login dell'altro utente

		String altroLoginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("altro").password("altra_password").build());

		MvcResult altroMvcResult = mockMvc
				.perform(post("/rest/auth/login").content(altroLoginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String altroResponseString = altroMvcResult.getResponse().getContentAsString();

		LoginDTO altroLoginDTO = objectMapper.readValue(altroResponseString, LoginDTO.class);

		Assertions.assertThat(altroLoginDTO.getAccessToken()).isNotBlank();
		Assertions.assertThat(altroLoginDTO.getTokenType()).isEqualTo("Bearer");

		String altroAccessToken = altroLoginDTO.getAccessToken();

		// recupero i dati utente con il webservice me
		MvcResult altroMvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + altroAccessToken))
				.andExpect(status().isOk()).andReturn();

		String altroResponseStringMe = altroMvcResultMe.getResponse().getContentAsString();

		UtenteDTO altroUtenteDTO = objectMapper.readValue(altroResponseStringMe, UtenteDTO.class);

		// faccio la richiesta usando come id quello dell'altro utente
		UtenteReq updateRequest = UtenteReq.builder().username("nuovo_username").id(altroUtenteDTO.getId()).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		// come token di autenticazione uso quello di utente
		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
		
		
		//verifico che non ci sia un utente con il nuovo username
		Assertions.assertThat(utenteRepository.existsByUsername("nuovo_username")).isFalse();

		// recupero direttamente l' utente da repository mi aspetto non sia cambiato
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(utenteAggiornato.getUsername()).isNotEqualTo("nuovo_username");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();

		// recupero direttamente l'altro utente da repository mi aspetto non sia
		// cambiato
		Utente altroUtenteAggiornato = utenteRepository.findById(altroUtenteDTO.getId())
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(altroUtenteAggiornato.getUsername()).isEqualTo("altro");
		Assertions.assertThat(altroUtenteAggiornato.getUsername()).isNotEqualTo("nuovo_username");
		Assertions.assertThat(passwordEncoder.matches("altra_password", altroUtenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(altroUtenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(altroUtenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(altroUtenteAggiornato.getEmail()).isEqualTo("altro@example.com");
		Assertions.assertThat(altroUtenteAggiornato.getIndirizzo()).isBlank();
	}
	
	/*
	 * Creo un utente e gli faccio aggiornare il suo ruolo ad {@link Ruolo#UTENTE} basandomi sul login,
	 * mi aspetto funzioni, anche perché in realtà non cambia
	 * 
	 * @throws Exception
	 *
	@Test
	public void updateRoleToUserTestRecognizingByLogin() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		UtenteReq updateRequest = UtenteReq.builder().ruolo(Ruolo.UTENTE).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// recupero direttamente l'utente da repository
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();
	}

	/*
	 * Creo un utente e gli faccio aggiornare il suo ruolo ad {@link Ruolo#UTENTE} basandomi sull'id utente
	 * ma dovrebbe essere lo stesso utente loggato, mi aspetto che funzioni anche perché il ruolo non cambia
	 * 
	 * @throws Exception
	 *
	@Test
	public void updateRoleToUserTestRecognizingById() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		// recupero i dati utente con il webservice me
		MvcResult mvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isOk()).andReturn();

		String responseStringMe = mvcResultMe.getResponse().getContentAsString();

		UtenteDTO utenteDTO = objectMapper.readValue(responseStringMe, UtenteDTO.class);

		UtenteReq updateRequest = UtenteReq.builder().ruolo(Ruolo.UTENTE).id(utenteDTO.getId()).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// recupero direttamente l'utente da repository
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();
	}

	/*
	 * Creo due utenti senza privilegi amministrativi e provo ad aggiornare
	 * il ruolo dell'altro ad {@link Ruolo#UTENTE} basandomi sull'id utente, mi aspetto vada in errore perché non consentito di cambiare
	 * il ruolo a qualcun altro, ma essendo comunque sempre ruolo utente, non cambia
	 * 
	 * @throws Exception
	 *
	@Test
	public void updateRoleToUserTestRecognizingByIdWrongId() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		String altroUtenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("altro")
				.password("altra_password").email("altro@example.com").build());
		mockMvc.perform(
				post("/rest/utente/public/create").content(altroUtenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login dell'altro utente

		String altroLoginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("altro").password("altra_password").build());

		MvcResult altroMvcResult = mockMvc
				.perform(post("/rest/auth/login").content(altroLoginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String altroResponseString = altroMvcResult.getResponse().getContentAsString();

		LoginDTO altroLoginDTO = objectMapper.readValue(altroResponseString, LoginDTO.class);

		Assertions.assertThat(altroLoginDTO.getAccessToken()).isNotBlank();
		Assertions.assertThat(altroLoginDTO.getTokenType()).isEqualTo("Bearer");

		String altroAccessToken = altroLoginDTO.getAccessToken();

		// recupero i dati utente con il webservice me
		MvcResult altroMvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + altroAccessToken))
				.andExpect(status().isOk()).andReturn();

		String altroResponseStringMe = altroMvcResultMe.getResponse().getContentAsString();

		UtenteDTO altroUtenteDTO = objectMapper.readValue(altroResponseStringMe, UtenteDTO.class);

		// faccio la richiesta usando come id quello dell'altro utente
		UtenteReq updateRequest = UtenteReq.builder().ruolo(Ruolo.UTENTE).id(altroUtenteDTO.getId()).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		// come token di autenticazione uso quello di utente
		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

		// recupero direttamente l' utente da repository mi aspetto non sia cambiato
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();

		// recupero direttamente l'altro utente da repository mi aspetto non sia
		// cambiato
		Utente altroUtenteAggiornato = utenteRepository.findByUsername("altro")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(altroUtenteAggiornato.getUsername()).isEqualTo("altro");
		Assertions.assertThat(passwordEncoder.matches("altra_password", altroUtenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(altroUtenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(altroUtenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(altroUtenteAggiornato.getEmail()).isEqualTo("altro@example.com");
		Assertions.assertThat(altroUtenteAggiornato.getIndirizzo()).isBlank();
	}

	
	/*
	 * Creo un utente e gli faccio aggiornare il suo ruolo ad {@link Ruolo#ADMIN} basandomi sul login,
	 * mi aspetto fallisca, perché un utente non può cambiarsi il ruolo in amministratore
	 * 
	 * @throws Exception
	 *
	@Test
	public void updateRoleToAdminTestRecognizingByLogin() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		UtenteReq updateRequest = UtenteReq.builder().ruolo(Ruolo.ADMIN).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// recupero direttamente l'utente da repository
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isNotEqualTo(Ruolo.ADMIN);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();
	}

	/*
	 * Creo un utente e gli faccio aggiornare il suo ruolo ad {@link Ruolo#ADMIN} basandomi sull'id utente
	 * ma dovrebbe essere lo stesso utente loggato, mi aspetto che fallisca perché un utente non può cambarsi ruolo in amministratore
	 * 
	 * @throws Exception
	 *
	@Test
	public void updateRoleToAdminTestRecognizingById() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		// recupero i dati utente con il webservice me
		MvcResult mvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isOk()).andReturn();

		String responseStringMe = mvcResultMe.getResponse().getContentAsString();

		UtenteDTO utenteDTO = objectMapper.readValue(responseStringMe, UtenteDTO.class);

		UtenteReq updateRequest = UtenteReq.builder().ruolo(Ruolo.ADMIN).id(utenteDTO.getId()).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// recupero direttamente l'utente da repository
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isNotEqualTo(Ruolo.ADMIN);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();
	}

	/*
	 * Creo due utenti senza privilegi amministrativi e provo ad aggiornare
	 * il ruolo dell'altro ad {@link Ruolo#ADMIN} basandomi sull'id utente, mi aspetto vada in errore perché non consentito di cambiare
	 * il ruolo a qualcun altro, e che i ruoli rimangano sempre utente
	 * 
	 * @throws Exception
	 *
	@Test
	public void updateRoleToAdminTestRecognizingByIdWrongId() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		String altroUtenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("altro")
				.password("altra_password").email("altro@example.com").build());
		mockMvc.perform(
				post("/rest/utente/public/create").content(altroUtenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login dell'altro utente

		String altroLoginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("altro").password("altra_password").build());

		MvcResult altroMvcResult = mockMvc
				.perform(post("/rest/auth/login").content(altroLoginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String altroResponseString = altroMvcResult.getResponse().getContentAsString();

		LoginDTO altroLoginDTO = objectMapper.readValue(altroResponseString, LoginDTO.class);

		Assertions.assertThat(altroLoginDTO.getAccessToken()).isNotBlank();
		Assertions.assertThat(altroLoginDTO.getTokenType()).isEqualTo("Bearer");

		String altroAccessToken = altroLoginDTO.getAccessToken();

		// recupero i dati utente con il webservice me
		MvcResult altroMvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + altroAccessToken))
				.andExpect(status().isOk()).andReturn();

		String altroResponseStringMe = altroMvcResultMe.getResponse().getContentAsString();

		UtenteDTO altroUtenteDTO = objectMapper.readValue(altroResponseStringMe, UtenteDTO.class);

		// faccio la richiesta usando come id quello dell'altro utente
		UtenteReq updateRequest = UtenteReq.builder().ruolo(Ruolo.ADMIN).id(altroUtenteDTO.getId()).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		// come token di autenticazione uso quello di utente
		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

		// recupero direttamente l' utente da repository mi aspetto non sia cambiato
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isNotEqualTo(Ruolo.ADMIN);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();

		// recupero direttamente l'altro utente da repository mi aspetto non sia
		// cambiato
		Utente altroUtenteAggiornato = utenteRepository.findByUsername("altro")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(altroUtenteAggiornato.getUsername()).isEqualTo("altro");
		Assertions.assertThat(passwordEncoder.matches("altra_password", altroUtenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(altroUtenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(altroUtenteAggiornato.getRuolo()).isNotEqualTo(Ruolo.ADMIN);
		Assertions.assertThat(altroUtenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(altroUtenteAggiornato.getEmail()).isEqualTo("altro@example.com");
		Assertions.assertThat(altroUtenteAggiornato.getIndirizzo()).isBlank();
	}
	
	
	/*
	 * Creo un utente e gli faccio aggiornare il suo credito basandomi sul login,
	 * mi aspetto fallisca
	 * 
	 * @throws Exception
	 *
	@Test
	public void updateCreditTestRecognizingByLogin() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		UtenteReq updateRequest = UtenteReq.builder().credito(BigDecimal.valueOf(1000)).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// recupero direttamente l'utente da repository
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isNotEqualTo(BigDecimal.valueOf(1000));
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();
	}

	/*
	 * Creo un utente e gli faccio aggiornare il suo credito basandomi sull'id utente
	 * ma dovrebbe essere lo stesso utente loggato, mi aspetto che fallisca
	 * 
	 * @throws Exception
	 *
	@Test
	public void updateCreditTestRecognizingById() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		// recupero i dati utente con il webservice me
		MvcResult mvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isOk()).andReturn();

		String responseStringMe = mvcResultMe.getResponse().getContentAsString();

		UtenteDTO utenteDTO = objectMapper.readValue(responseStringMe, UtenteDTO.class);

		UtenteReq updateRequest = UtenteReq.builder().credito(BigDecimal.valueOf(1000)).id(utenteDTO.getId()).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// recupero direttamente l'utente da repository
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isNotEqualTo(BigDecimal.valueOf(1000));
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();
	}

	/*
	 * Creo due utenti senza privilegi amministrativi e provo ad aggiornare
	 * il credito dell'altro basandomi sull'id utente, mi aspetto vada in errore
	 * 
	 * @throws Exception
	 *
	@Test
	public void updateCreditTestRecognizingByIdWrongId() throws Exception {

		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password")
				.email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login

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

		String altroUtenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("altro")
				.password("altra_password").email("altro@example.com").build());
		mockMvc.perform(
				post("/rest/utente/public/create").content(altroUtenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		// faccio il login dell'altro utente

		String altroLoginReqJSON = objectMapper
				.writeValueAsString(LoginReq.builder().username("altro").password("altra_password").build());

		MvcResult altroMvcResult = mockMvc
				.perform(post("/rest/auth/login").content(altroLoginReqJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(cookie().exists("refreshToken")).andReturn();

		String altroResponseString = altroMvcResult.getResponse().getContentAsString();

		LoginDTO altroLoginDTO = objectMapper.readValue(altroResponseString, LoginDTO.class);

		Assertions.assertThat(altroLoginDTO.getAccessToken()).isNotBlank();
		Assertions.assertThat(altroLoginDTO.getTokenType()).isEqualTo("Bearer");

		String altroAccessToken = altroLoginDTO.getAccessToken();

		// recupero i dati utente con il webservice me
		MvcResult altroMvcResultMe = mockMvc
				.perform(get("/rest/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + altroAccessToken))
				.andExpect(status().isOk()).andReturn();

		String altroResponseStringMe = altroMvcResultMe.getResponse().getContentAsString();

		UtenteDTO altroUtenteDTO = objectMapper.readValue(altroResponseStringMe, UtenteDTO.class);

		// faccio la richiesta usando come id quello dell'altro utente
		UtenteReq updateRequest = UtenteReq.builder().credito(BigDecimal.valueOf(1000)).id(altroUtenteDTO.getId()).build();

		String updateRequestJson = objectMapper.writeValueAsString(updateRequest);

		// come token di autenticazione uso quello di utente
		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

		// recupero direttamente l' utente da repository mi aspetto non sia cambiato
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isNotEqualTo(BigDecimal.valueOf(1000));
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();

		// recupero direttamente l'altro utente da repository mi aspetto non sia
		// cambiato
		Utente altroUtenteAggiornato = utenteRepository.findByUsername("altro")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(altroUtenteAggiornato.getUsername()).isEqualTo("altro");
		Assertions.assertThat(passwordEncoder.matches("altra_password", altroUtenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(altroUtenteAggiornato.getCredito()).isNotEqualTo(BigDecimal.valueOf(1000));
		Assertions.assertThat(altroUtenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(altroUtenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(altroUtenteAggiornato.getEmail()).isEqualTo("altro@example.com");
		Assertions.assertThat(altroUtenteAggiornato.getIndirizzo()).isBlank();
	}
	
	//*/
	
	//TODO testare login su endpoint updae amministrativo da utente non amministratore
}
