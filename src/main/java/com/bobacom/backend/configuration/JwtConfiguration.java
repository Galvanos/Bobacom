package com.bobacom.backend.configuration;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
@Configuration
public class JwtConfiguration {

	 /*
     * converter per dire a Spring Security di leggere roles
     */  
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter rolesConverter = new JwtGrantedAuthoritiesConverter();
        rolesConverter.setAuthoritiesClaimName("roles");
        rolesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(rolesConverter);

        return converter;
    }
    
    @Bean
    JwtDecoder jwtDecoder(@Value("${app.jwt.secret}") String secret) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

        return NimbusJwtDecoder
            .withSecretKey(key)
            .macAlgorithm(MacAlgorithm.HS512)    // algoritmo di decod -> deve essere identica al sevizio
            .build();
    }
}
