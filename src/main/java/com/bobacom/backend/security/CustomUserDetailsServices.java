package com.bobacom.backend.security;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

import com.bobacom.backend.dto.output.UtenteDTO;
import com.bobacom.backend.service.interfaces.IUtenteService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomUserDetailsServices {

	private final IUtenteService utenteService;
	private final PasswordEncoder getPasswordEncoder;
	
	
	public InMemoryUserDetailsManager loadUser() throws Exception {
		log.debug("loadUser ....");
		
		List<UtenteDTO> list;
		try {
			list = utenteService.list();
		} catch (Exception e) {
			//si può solo loggare un errore perché si tratta di cosa assai grave non rimediabile
			log.error(e.getMessage());
			throw e;
		}
		
		List<UserDetails> userDetailsList = list.stream()
				.map(usr -> User.withUsername(usr.getUsername())
						.password(getPasswordEncoder.encode(usr.getPassword().toString()))
						.roles(usr.getRuolo().name())
						.build())
				.collect(Collectors.toList());
		
		return new InMemoryUserDetailsManager(userDetailsList);
	}
}
