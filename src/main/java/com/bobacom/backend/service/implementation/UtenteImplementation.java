package com.bobacom.backend.service.implementation;

import com.bobacom.backend.controller.AuthController;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bobacom.backend.dto.input.AddCreditReq;
import com.bobacom.backend.dto.input.UtenteReq;
import com.bobacom.backend.dto.output.UtenteDTO;
import com.bobacom.backend.enums.Ruolo;
import com.bobacom.backend.exceptions.AcademyException;
import com.bobacom.backend.exceptions.ForbiddenException;
import com.bobacom.backend.exceptions.UnauthorizedException;
import com.bobacom.backend.exceptions.UserNotFoundException;
import com.bobacom.backend.mapping.UtenteMap;
import com.bobacom.backend.model.Utente;
import com.bobacom.backend.repository.IUtenteRepository;
import com.bobacom.backend.service.interfaces.IUtenteService;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UtenteImplementation implements IUtenteService {


	private final IUtenteRepository repository;
	
	private final PasswordEncoder passwordEncoder;
	
	
	/**
	 * Valore del credito di default, impostato tramite file di properties ed eventualmente tramite web service,
	 * potrebbe venir salvato in database usando la tabella chiave valore, se non impostato vale 0
	 * 
	 */
	@Getter
	@Setter
	@Value(value = "${app.credito.valoreDefault:0}")
	private BigDecimal creditoDefault;

	
	/**
	 * Valore inserito per simulare una transazione sicura per aggiungere il credito, 
	 * si verifica che un determinato campo del JSON corrisponda a questo valore,
	 * se non impostata nel file di properties viene lasciata come stringa vuota
	 */
	@Getter
	@Value(value = "${app.credito.secret:}")
	private String creditoSecret;

	
	
	@Transactional
	@Override
	public UtenteDTO create(UtenteReq req) throws Exception {
		if(req == null) {
			throw new AcademyException("utente non fornito");
		}		
		req.setId(null);//annullo un eventuale id utente visto che siamo in creazione
		//impongo in creazione che abbia un role (user  se assente) visto che serve nella gestione utenti
		req.setRuolo(Optional.ofNullable(req).map(UtenteReq::getRuolo).orElse(Ruolo.UTENTE));
		req.setCredito(Optional.ofNullable(req).map(UtenteReq::getCredito).orElse(creditoDefault));
		boolean alreadyExistsUsername = repository.existsByUsername(req.getUsername());
		if(alreadyExistsUsername) {
			throw new AcademyException("username già esistente");
		}
		String encodedPassword = passwordEncoder.encode(req.getPassword());
		Utente utente = Utente.builder()
		      .credito(req.getCredito())
		      .email(req.getEmail())
		      .indirizzo(req.getIndirizzo())
		      .password(encodedPassword)
		      .ruolo(req.getRuolo())
		      .username(req.getUsername())
		      .build();
		utente = repository.save(utente);
		
		return UtenteMap.buildUtenteDTO(utente, false);
	}
	

	@Override
	public UtenteDTO createByUser(UtenteReq req) throws Exception {
		req.setRuolo(Ruolo.UTENTE);
		req.setCredito(creditoDefault);
		return create(req);
	}
	
	
	@Transactional
	@Override
	public UtenteDTO update(UtenteReq req) throws Exception {
		if(req == null) {
			throw new AcademyException("utente non fornito");
		}
		Integer id = req.getId();
		if(id == null) {
			throw new AcademyException("id utente non fornito");
		}
		Utente storedUser = repository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("utente non trovato"));
		String formerUsername = storedUser.getUsername();
		String requestUsername = req.getUsername();
		if(requestUsername != null) {
			if(!Objects.equals(requestUsername, formerUsername)) {
				boolean alreadyExistsUsername = repository.existsByUsername(requestUsername);
				if(alreadyExistsUsername) {
					throw new AcademyException("username già esistente");
				}else {
					//verifico che l'utente non stia cambiando il suo stesso nome utente, il che sarebbe un problema per il JWT  già acquisito
					Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
					if(authentication != null) {
						if(authentication.isAuthenticated()) {
							String authenticatedUsername = authentication.getName();
							if(Objects.equals(formerUsername, authenticatedUsername)) {
								throw new AcademyException("cambio username non consentito");
							}
						}
					}
					storedUser.setUsername(requestUsername);
				}
			}
		}else {
			//per updateUtenteForAuthentication devo forzare lo username, probabilmente non servirà più
			req.setUsername(formerUsername);
		}
		
		String encodedPassword = storedUser.getPassword();
		
		String password = req.getPassword();
		if(password != null) {
			encodedPassword = passwordEncoder.encode(password);
			storedUser.setPassword(encodedPassword);
		}
		
		Optional.ofNullable(req).map(UtenteReq::getCredito).ifPresent(storedUser::setCredito);
		Optional.ofNullable(req).map(UtenteReq::getEmail).ifPresent(storedUser::setEmail);
		Optional.ofNullable(req).map(UtenteReq::getIndirizzo).ifPresent(storedUser::setIndirizzo);
		Optional.ofNullable(req).map(UtenteReq::getRuolo).ifPresent(storedUser::setRuolo);
		
		Utente updatedUser = repository.save(storedUser);
		
		return UtenteMap.buildUtenteDTO(updatedUser, false);
	}
	
	@Override
	public UtenteDTO updateByUser(UtenteReq req) throws Exception {
		Integer id = req.getId();
		if(id == null) {
			throw new AcademyException("id utente non fornito");
		}
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
				throw new UnauthorizedException("utente non autorizzato");
			}
		}
		//annullo credito, ruolo e username che un utente non può cambiarsi
		req.setCredito(null);
		req.setRuolo(null);
		req.setUsername(null);//non viene consentito il cambio di username per non invalidare il JWT già acquisito
		return update(req);
	}

	@Override
	public UtenteDTO getById(Integer id) throws Exception {
		Utente storedUser = repository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("utente non trovato"));
		return UtenteMap.buildUtenteDTO(storedUser, false);
		
	}

	@Override
	public List<UtenteDTO> list() throws Exception {
		List<Utente> users = repository.findAll();
		users = Optional.ofNullable(users).orElse(Collections.emptyList());
		return UtenteMap.buildUtenteDTOList(users, false);
	}


	@Override
	public UtenteDTO delete(Integer id) throws Exception {
		Utente storedUser = repository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("utente non trovato"));
		repository.delete(storedUser);
		return UtenteMap.buildUtenteDTO(storedUser, false);
		
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
				.orElseThrow(() -> new UserNotFoundException("utente non trovato"));
		BigDecimal credito = storedUser.getCredito();
		if(credito == null) {
			credito = BigDecimal.ZERO;
		}
		BigDecimal addingCredit = addCredReq.getCredit();
		BigDecimal resultingCredit = credito.add(addingCredit);
		storedUser.setCredito(resultingCredit);
		storedUser = repository.save(storedUser);
		return UtenteMap.buildUtenteDTO(storedUser, false);
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
				throw new UnauthorizedException("utente non autorizzato");
			}
		}
		return storedUser;
	}

	@Override
	public UtenteDTO getByUsername(String username) throws Exception {
		Utente storedUser = repository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("utente non trovato"));
		
		return UtenteMap.buildUtenteDTO(storedUser, false);
		
	}

	@Override
	public UtenteDTO getByUsernameByUser(String username) throws Exception {
		UtenteDTO storedUser = getByUsername(username);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication != null) {
			if(authentication.isAuthenticated()) {
				String authenticatedUsername = authentication.getName();
				Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
				boolean isAdmin = authorities.stream().map(t -> t.getAuthority()).anyMatch(t -> Objects.equals(t, "ROLE_"+Ruolo.ADMIN.name()));
				boolean differentUsername = !Objects.equals(authenticatedUsername, storedUser.getUsername());
				if(!isAdmin && differentUsername) {
					throw new ForbiddenException("utente non autorizzato");
				}
			}else {
				throw new UnauthorizedException("utente non autorizzato");
			}
		}
		
		return storedUser;
	}
	
	
	

}
