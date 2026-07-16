package com.bobacom.backend.service.implementation;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.transaction.annotation.Transactional;

import com.bobacom.backend.dto.input.AddCreditReq;
import com.bobacom.backend.dto.input.UtenteReq;
import com.bobacom.backend.dto.output.UtenteDTO;
import com.bobacom.backend.enums.Ruolo;
import com.bobacom.backend.exceptions.AcademyException;
import com.bobacom.backend.exceptions.ForbiddenException;
import com.bobacom.backend.model.Utente;
import com.bobacom.backend.repository.IUtenteRepository;
import com.bobacom.backend.service.interfaces.IUtenteService;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class UtenteImplementation implements IUtenteService {

	private final IUtenteRepository repository;
	
	private final PasswordEncoder getPasswordEncoder;
	private final InMemoryUserDetailsManager inMemoryUserDetailsManager;
	
	
	/**
	 * Valore del credito di default, impostato tramite file di properties ed eventualmente tramite web service,
	 * potrebbe venir salvato in database usando la tabella chiave valore, se non impostato vale 0
	 * 
	 */
	@Getter
	@Setter
	@Value(value = "${credito.valoreDefault:0}")
	private BigDecimal creditoDefault;
	
	
	/**
	 * Valore inserito per simulare una transazione sicura per aggiungere il credito, 
	 * si verifica che un determinato campo del JSON corrisponda a questo valore,
	 * se non impostata nel file di properties viene lasciata come stringa vuota
	 */
	@Getter
	@Value(value = "${credito.secret:}")
	private String creditoSecret;
	
	@Transactional
	@Override
	public UtenteDTO create(UtenteReq req) throws Exception {
		req.setId(null);
		//impongo in creazione che abbia un role (user  se assente) visto che serve nela gestione utenti
		req.setRuolo(Optional.ofNullable(req).map(UtenteReq::getRuolo).orElse(Ruolo.UTENTE));
		req.setCredito(Optional.ofNullable(req).map(UtenteReq::getCredito).orElse(BigDecimal.ZERO));
		boolean alreadyExistsUsername = repository.existsByUsername(req.getUsername());
		if(alreadyExistsUsername) {
			throw new AcademyException("username già esistente");
		}
		String encodedPassword = getPasswordEncoder.encode(req.getPassword());
		Utente utente = Utente.builder()
		      .credito(req.getCredito())
		      .email(req.getEmail())
		      .indirizzo(req.getIndirizzo())
		      .password(encodedPassword)
		      .ruolo(req.getRuolo())
		      .username(req.getUsername())
		      .build();
		utente = repository.save(utente);
		updateUtenteForAuthentication(req, encodedPassword);
		return UtenteDTO.builder()
				.credito(utente.getCredito())
				.email(utente.getEmail())
				.id(utente.getId())
				.indirizzo(utente.getIndirizzo())
				.password(utente.getPassword())//in caso verrà annullata nel rest controller
				.ruolo(utente.getRuolo())
				.username(utente.getUsername())
				.build();
	}
	

	@Override
	public UtenteDTO createByUser(UtenteReq req) throws Exception {
		req.setRuolo(Ruolo.UTENTE);
		req.setCredito(creditoDefault);
		return create(req);
	}
	
	
	@Transactional
	@Override
	public void update(UtenteReq req) throws Exception {
		if(req == null) {
			throw new AcademyException("utente non fornito");
		}
		Utente storedUser = repository.findById(req.getId())
				.orElseThrow(() -> new AcademyException("utente non trovato"));
		String requestUsername = req.getUsername();
		if(requestUsername != null) {
			if(Objects.equals(requestUsername, storedUser.getUsername())) {
				req.setUsername(null);//lo username non cambia, quindi non modifico
			}else {
				boolean alreadyExistsUsername = repository.existsByUsername(requestUsername);
				if(alreadyExistsUsername) {
					throw new AcademyException("username già esistente");
				}else {
					storedUser.setUsername(requestUsername);
				}
			}
		}
		
		String encodedPassword = storedUser.getPassword();
		
		String password = req.getPassword();
		if(password != null) {
			encodedPassword = getPasswordEncoder.encode(password);
			storedUser.setPassword(encodedPassword);
		}
		
		Optional.ofNullable(req).map(UtenteReq::getCredito).ifPresent(storedUser::setCredito);
		Optional.ofNullable(req).map(UtenteReq::getEmail).ifPresent(storedUser::setEmail);
		Optional.ofNullable(req).map(UtenteReq::getIndirizzo).ifPresent(storedUser::setIndirizzo);
		Optional.ofNullable(req).map(UtenteReq::getRuolo).ifPresent(storedUser::setRuolo);
		
		repository.save(storedUser);
		
		//TODO gestire eventuale cambio di username
		updateUtenteForAuthentication(req, encodedPassword);
	}
	
	@Override
	public void updateByUser(UtenteReq req) throws Exception {
		//TODO verificare che ad aggiornare sia l'utente stesso che invoca
		req.setCredito(null);
		req.setRuolo(null);
		update(req);
	}

	@Override
	public UtenteDTO getById(Integer id) throws Exception {
		Utente storedUser = repository.findById(id)
				.orElseThrow(() -> new AcademyException("utente non trovato"));
		
		return UtenteDTO.builder()
				.credito(storedUser.getCredito())
				.email(storedUser.getEmail())
				.id(storedUser.getId())
				.indirizzo(storedUser.getIndirizzo())
				.password(storedUser.getPassword())//TODO da capire per la password anche perché in database sarà criptata, per ora la si tiene, in caso la si annulla nel rest controller
				.ruolo(storedUser.getRuolo())
				.username(storedUser.getUsername())
				.build();
		
	}

	@Override
	public List<UtenteDTO> list() throws Exception {
		List<Utente> users = repository.findAll();
		users = Optional.ofNullable(users).orElse(Collections.emptyList());
		return users.stream().map(storedUser -> 
			 UtenteDTO.builder()
					.credito(storedUser.getCredito())
					.email(storedUser.getEmail())
					.id(storedUser.getId())
					.indirizzo(storedUser.getIndirizzo())
					.password(storedUser.getPassword())//si tiene l'hash della password perché serve in creazione utenti inmemory, ma nel rest controller va annullata
					.ruolo(storedUser.getRuolo())
					.username(storedUser.getUsername())
					.build()
		).toList();
	}

	@Override
	public void delete(Integer id) throws Exception {
		Utente storedUser = repository.findById(id)
				.orElseThrow(() -> new AcademyException("utente non trovato"));
		repository.delete(storedUser);
		
	}

	@Transactional
	@Override
	public UtenteDTO addCredit(AddCreditReq addCredReq) throws Exception {
		Integer userId = addCredReq.getUserId();
		String secret = addCredReq.getSecret();
		if(Objects.equals(secret,creditoSecret)){
			throw new AcademyException("secret non valido");
		}
		Utente storedUser = repository.findById(userId)
				.orElseThrow(() -> new AcademyException("utente non trovato"));
		BigDecimal credito = storedUser.getCredito();
		if(credito == null) {
			credito = BigDecimal.ZERO;
		}
		BigDecimal addingCredit = addCredReq.getCredit();
		BigDecimal resultingCredit = credito.add(addingCredit);
		storedUser.setCredito(resultingCredit);
		storedUser = repository.save(storedUser);
		return UtenteDTO.builder()
				.credito(storedUser.getCredito())
				.email(storedUser.getEmail())
				.id(storedUser.getId())
				.indirizzo(storedUser.getIndirizzo())
				.password(storedUser.getPassword())//TODO da capire per la password anche perché in database sarà criptata, per ora la si tiene, in caso la si annulla nel rest controller
				.ruolo(storedUser.getRuolo())
				.username(storedUser.getUsername())
				.build();
	}


	@Override
	public UtenteDTO addCreditByUser(AddCreditReq addCreditReq) throws Exception {
		// TODO da capire come fare il filtraggio per l'utente loggato
		return addCredit(addCreditReq);
	}


	@Override
	public UtenteDTO getByIdByUser(Integer id) throws Exception {
		UtenteDTO storedUser = getById(id);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication != null) {
			if(authentication.isAuthenticated()) {
				String username = authentication.getName();
				Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
				boolean isAdmin = authorities.stream().map(t -> t.getAuthority()).anyMatch(t -> Objects.equals(t, "ROLE_"+Ruolo.ADMIN.name()));
				boolean differentUsername = !Objects.equals(username, storedUser.getUsername());
				if(!isAdmin && differentUsername) {
					throw new ForbiddenException("utente non autorizzato");
				}
			}else {
				//idealmente si dovrebbe mettere unauthorized anziché forbidden
				throw new ForbiddenException("utente non autorizzato");
			}
		}
		return storedUser;
	}
	
	
	public void updateUtenteForAuthentication(UtenteReq req, String encodedPassword) {
		if (inMemoryUserDetailsManager.userExists(req.getUsername())) {
			inMemoryUserDetailsManager.deleteUser(req.getUsername());
			log.debug("utente {} deleted", req.getUsername());
		}
		encodedPassword = Optional.ofNullable(encodedPassword).orElse(getPasswordEncoder.encode(req.getPassword()));
		inMemoryUserDetailsManager.createUser(User
				.withUsername(req.getUsername())
				.password(encodedPassword)
				.roles(req.getRuolo().toString())
				.build()
				);
		log.debug("User {} is created", req.getUsername());
	}

}
