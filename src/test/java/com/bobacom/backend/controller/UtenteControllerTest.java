package com.bobacom.backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
		utenteService.create(
				UtenteReq.builder().username("admin").password("admin_password").email("admin@example.com").ruolo(Ruolo.ADMIN).build());

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
		mockMvc.perform(post("/rest/utente/admin/create").header(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken).content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
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
	 * Creo un utente con i dati minimi usando l'endpoind amministrativo ma con un utente senza privilegi amministrativi, mi aspetto di venire bloccato
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateInAdminEndpointByNotAdmin() throws Exception {

		// creo un utente non amministrativo che farà login ma non potrà fare l'operazione
		utenteService.create(
				UtenteReq.builder().username("non_admin").password("non_admin_password").email("non_admin@example.com").build());

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
		mockMvc.perform(post("/rest/utente/admin/create").header(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken).content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

		Assertions.assertThat(utenteRepository.existsByUsername("utente")).isFalse();

	}
	
	/**
	 * Creo un utente con i dati minimi usando l'endpoind amministrativo senza fare login, mi aspetto di venire bloccato
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
	 * Creo un utente con lo stesso username di un utente già creato, mi aspetto
	 * vada in errore la creazione del secondo utente
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
		mockMvc.perform(post("/rest/utente/admin/create").header(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken).content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
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
				post("/rest/utente/admin/create").header(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken).content(utenteJSONDuplicato).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

	}

	/**
	 * Creo un utente impostando il creditoda parte dell'amministratore, mi aspetto che venga impostato il credito voluto dall'amministratore
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
		mockMvc.perform(post("/rest/utente/admin/create").header(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken).content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

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
	 * Creo un utente impostando come ruolo ADMIN da parte di un amministratore, 
	 * mi aspetto l'utente venga creato con ruolo ADMIN perché creato da amministratore
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
		mockMvc.perform(post("/rest/utente/admin/create").header(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken).content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

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
	 * Creo un utente impostando maxint come ID da parte di un amministratore, mi aspetto venga ignorato
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
		mockMvc.perform(post("/rest/utente/admin/create").header(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken).content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
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
	 * Creo un utente impostando l'indirizzo da parte di un amministratore, mi aspetto funzioni
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
		mockMvc.perform(post("/rest/utente/admin/create").header(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken).content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
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
	 * Creo un utente impostando senza username da parte di un amministratore, mi aspetto fallisca
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
		mockMvc.perform(post("/rest/utente/admin/create").header(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken).content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

	}

	/**
	 * Creo un utente impostando senza password da parte di un amministratore, mi aspetto fallisca
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
		mockMvc.perform(post("/rest/utente/admin/create").header(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken).content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		Assertions.assertThat(utenteRepository.existsByUsername("utente")).isFalse();

	}

	/**
	 * Creo un utente impostando senza email da parte di un amministratore, mi aspetto fallisca
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
		mockMvc.perform(post("/rest/utente/admin/create").header(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken).content(utenteJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		Assertions.assertThat(utenteRepository.existsByUsername("utente")).isFalse();

	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////

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
		
		mockMvc.perform(patch("/rest/utente/user/update").content(updateRequestJson).header(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
		
		//recupero direttamente l'utente da repository
		Utente utenteAggiornato = utenteRepository.findByUsername("utente")
				.orElseGet(() -> Assertions.fail("utente non trovato"));

		Assertions.assertThat(utenteAggiornato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteAggiornato.getPassword())).isTrue();
		Assertions.assertThat(utenteAggiornato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteAggiornato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteAggiornato.getEmail()).isEqualTo("email_aggiornata@example.com");
		Assertions.assertThat(utenteAggiornato.getIndirizzo()).isBlank();
	}
	
	

}
