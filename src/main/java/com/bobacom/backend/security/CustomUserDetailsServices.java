package com.bobacom.backend.security;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

import com.bobacom.backend.configuration.InMemorySecurityControl;
import com.bobacom.backend.dto.output.UtenteDTO;
import com.bobacom.backend.model.Utente;
import com.bobacom.backend.repository.IUtenteRepository;
import com.bobacom.backend.service.interfaces.IUtenteService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomUserDetailsServices implements UserDetailsService {

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
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 * @deprecated rimane fino alla rimozione dell'{@link InMemorySecurityControl}
	 */
	@Deprecated
	public InMemoryUserDetailsManager loadUser() throws Exception {
		log.debug("loadUser ....");
		
		List<Utente> list;
		try {
			list = utenteRepository.findAll();
		} catch (Exception e) {
			//si può solo loggare un errore perché si tratta di cosa assai grave non rimediabile
			log.error(e.getMessage());
			throw e;
		}
		
		List<UserDetails> userDetailsList = list.stream()
				.map(usr -> User.withUsername(usr.getUsername())
						.password(usr.getPassword())//in database è già criptata, non serve l'encoder
						.roles(usr.getRuolo().name())
						.build())
				.collect(Collectors.toList());
		
		return new InMemoryUserDetailsManager(userDetailsList);
	}
}
