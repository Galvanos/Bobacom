package com.bobacom.backend.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties.Apiversion.Use;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityControl {
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		/*
		http.authorizeHttpRequests((requests) -> requests
				.requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
				.requestMatchers("/login", "/registry", "/saveNuovoUtente").permitAll()
				.anyRequest().authenticated()				
				)
				.formLogin((form) -> form
						.loginPage("/login")
						.permitAll()
						)
				.logout((logout) -> logout.permitAll());
		return http.build();
		*/
		//temporaneamente non chiedo login e non faccio controlli csrf
		http
        // 1. Permetti l'accesso a qualsiasi richiesta senza autenticazione
        .authorizeHttpRequests(auth -> auth
            .anyRequest().permitAll()
        )
        // 2. Disabilita la protezione CSRF (essenziale per i RestController/POST/PUT)
        .csrf(csrf -> csrf.disable())
        
        // 3. Disabilita esplicitamente il login basato su form si lascia il basic nel caso un utente fornisca utente e password
        .formLogin(form -> form.disable());
        //.httpBasic(basic -> basic.disable());

		return http.build();
	}
	
	@Bean
	PasswordEncoder getPasswordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	
}
