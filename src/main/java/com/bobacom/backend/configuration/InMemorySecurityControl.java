package com.bobacom.backend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import com.bobacom.backend.security.CustomUserDetailsServices;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class InMemorySecurityControl {

	private final CustomUserDetailsServices customUserDetailsServices;
	
	@Bean
	InMemoryUserDetailsManager inMemoryUserDetailsManager() throws Exception {
		return customUserDetailsServices.loadUser();
	}
}
