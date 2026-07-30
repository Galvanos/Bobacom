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
	private IUtenteRepository utenteRepository;//il repository serve per verificare gli utenti creati
	
	@Autowired
	private IUtenteService utenteService;//il service serve per creare utenti amministrativi
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Value(value = "${app.credito.valoreDefault:0}")
	private BigDecimal creditoDefault;
	
	
	/**
	 * Creo un utente con i dati minimi
	 * @throws Exception
	 */
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
	
	/**
	 * Creo un utente con lo stesso username di un utente già creato, mi aspetto vada in errore la creazione del secondo utente
	 * @throws Exception
	 */
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
		
	/**
	 * Creo un utente impostando il credito, mi aspetto che venga ignorato
	 * @throws Exception
	 */
	@Test
	public void testCreateUserWithCredit() throws Exception {
		
		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password").email("utente@example.com").credito(BigDecimal.valueOf(1000)).build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
		
		Utente utenteCreato = utenteRepository.findByUsername("utente").orElseGet(() -> Assertions.fail("utente non trovato"));
		
		Assertions.assertThat(utenteCreato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteCreato.getPassword())).isTrue() ;
		Assertions.assertThat(utenteCreato.getCredito()).isNotEqualTo(BigDecimal.valueOf(1000));
		Assertions.assertThat(utenteCreato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteCreato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteCreato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteCreato.getIndirizzo()).isBlank();
		
	}
	
	/**
	 * Creo un utente impostando come ruolo ADMIN, mi aspetto l'utente venga creato comunque con ruolo user
	 * @throws Exception
	 */
	@Test
	public void testCreateUserWithAdminRole() throws Exception {
		
		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password").email("utente@example.com").ruolo(Ruolo.ADMIN).build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
		
		Utente utenteCreato = utenteRepository.findByUsername("utente").orElseGet(() -> Assertions.fail("utente non trovato"));
		
		Assertions.assertThat(utenteCreato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteCreato.getPassword())).isTrue() ;
		Assertions.assertThat(utenteCreato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteCreato.getRuolo()).isNotEqualTo(Ruolo.ADMIN);
		Assertions.assertThat(utenteCreato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteCreato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteCreato.getIndirizzo()).isBlank();
		
	}
	
	/**
	 * Creo un utente impostando maxint come ID, mi aspetto venga ignorato
	 * @throws Exception
	 */
	@Test
	public void testCreateUserWithUserId() throws Exception {
		
		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password").email("utente@example.com").id(Integer.MAX_VALUE).build());//Uso max value, si assume che tra i vari test non raggiunga un id così alto
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
		
		Utente utenteCreato = utenteRepository.findByUsername("utente").orElseGet(() -> Assertions.fail("utente non trovato"));
		
		Assertions.assertThat(utenteCreato.getId()).isNotEqualTo(Integer.MAX_VALUE);
		Assertions.assertThat(utenteCreato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteCreato.getPassword())).isTrue() ;
		Assertions.assertThat(utenteCreato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteCreato.getRuolo()).isNotEqualTo(Ruolo.ADMIN);
		Assertions.assertThat(utenteCreato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteCreato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteCreato.getIndirizzo()).isBlank();
		
	}
	
	/**
	 * Creo un utente impostando l'indirizzo, mi aspetto funzioni
	 * @throws Exception
	 */
	@Test
	public void testCreateUserWithAddress() throws Exception {
		
		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password").email("utente@example.com").indirizzo("Via Torino 1").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
		
		Utente utenteCreato = utenteRepository.findByUsername("utente").orElseGet(() -> Assertions.fail("utente non trovato"));
		
		Assertions.assertThat(utenteCreato.getUsername()).isEqualTo("utente");
		Assertions.assertThat(passwordEncoder.matches("password", utenteCreato.getPassword())).isTrue() ;
		Assertions.assertThat(utenteCreato.getCredito()).isEqualTo(creditoDefault);
		Assertions.assertThat(utenteCreato.getRuolo()).isEqualTo(Ruolo.UTENTE);
		Assertions.assertThat(utenteCreato.getEmail()).isEqualTo("utente@example.com");
		Assertions.assertThat(utenteCreato.getIndirizzo()).isNotBlank();
		Assertions.assertThat(utenteCreato.getIndirizzo()).isEqualTo("Via Torino 1");
		
	}
	
	/**
	 * Creo un utente impostando senza username, mi aspetto fallisca
	 * @throws Exception
	 */
	@Test
	public void testCreateUserWithoutUsername() throws Exception {
		
		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().password("password").email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
		
	}
	
	/**
	 * Creo un utente impostando senza password, mi aspetto fallisca
	 * @throws Exception
	 */
	@Test
	public void testCreateUserWithoutPassword() throws Exception {
		
		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").email("utente@example.com").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
		
		Assertions.assertThat(utenteRepository.existsByUsername("utente")).isFalse();
		
	}
	
	/**
	 * Creo un utente impostando senza email, mi aspetto fallisca
	 * @throws Exception
	 */
	@Test
	public void testCreateUserWithoutEmail() throws Exception {
		
		String utenteJSON = objectMapper.writeValueAsString(UtenteDTO.builder().username("utente").password("password").build());
		mockMvc.perform(post("/rest/utente/public/create").content(utenteJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
		
		Assertions.assertThat(utenteRepository.existsByUsername("utente")).isFalse();
		
	}

}
