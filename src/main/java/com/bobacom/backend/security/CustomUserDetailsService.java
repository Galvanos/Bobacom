package com.bobacom.backend.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bobacom.backend.model.Utente;
import com.bobacom.backend.repository.IUtenteRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final IUtenteRepository utenteRepository;//usato repository perché se si usasse il service per la list si creerebbe una dipendenza circolare per aggiornare l'utente in memory
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.debug("loadUserByUsername: {}", username);
		
		Utente byUsername = utenteRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("utente non trovato"));
		
		return User.builder()
				.username(byUsername.getUsername())
				.password(byUsername.getPassword())//password giá criptata su db, per cui non serve criptarla
				.roles(byUsername.getRuolo().name())
				.build();
	}
	
}
