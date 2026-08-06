package com.bobacom.backend.security.implementation;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.bobacom.backend.security.interfaces.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JwtImpl implements JwtService {
	private final SecretKey key; // la secret key é messa dentro il file di properties. Si fa generare
								 // Linux/Mac : openssl rand -base64 64
								 // Windows : [Convert]::ToBase64String((1..64 | ForEach-Object {Get-Random
								 // -Maximum 256}))

	@Value("${app.jwt.access-token-expiration-seconds}")
	private long accessTokenExpirationSeconds;
	@Value("${app.jwt.refresh-token-expiration-days}")
	private long refreshTokenExpirationDays;

	
	public JwtImpl(@Value("${app.jwt.secret}") String secret) {
		log.debug("JwtImpl : {}", secret);
		try {
			this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));		
		} catch (Exception e) {
			throw new IllegalStateException(
					"Configurazione JWT non valida: controlla app.jwt.secret. Deve essere Base64 valido e abbastanza lungo.",
					e);
		}
	}

	@Override
	public String generateAccessToken(Authentication authentication) {
		Instant now = Instant.now();

		List<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

		String t = Jwts.builder()
	                .subject(authentication.getName())
	                .claim("roles", roles)
	                .issuedAt(Date.from(now))
	                .expiration(Date.from(now.plusSeconds(accessTokenExpirationSeconds)))
	                .signWith(key, Jwts.SIG.HS512)
	                .compact();
		 
		 log.debug("Token : {}",t);
	        
	     return t;
		 
	}

	@Override
	public String generateRefreshToken(Authentication authentication) {
		Instant now = Instant.now();

		List<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
		
		
		String t = Jwts.builder()
		    .subject(authentication.getName())
		    .claim("tokenType", "REFRESH")
		    .issuedAt(Date.from(now))
		    .expiration(Date.from(now.plus(refreshTokenExpirationDays,ChronoUnit.DAYS)))
		    .signWith(key, Jwts.SIG.HS512)
		    .compact();
		 
		 log.debug("Token : {}",t);
	        
	     return t;
		 
	}

	@Override
	public boolean isValidRefreshToken(String token) throws Exception{
	    try {
	        Claims claims = extractAllClaims(token);

	        String tokenType = claims.get(
	                "tokenType",
	                String.class
	        );

	        return "REFRESH".equals(tokenType)
	                && claims.getExpiration().after(new Date());

	    } catch (JwtException | IllegalArgumentException e) {
	        return false;
	    }
	}
	
	@Override
	public String extractUsername(String token) {
	    return extractAllClaims(token).getSubject();
	}
	
	
	private Claims extractAllClaims(String token) {
	    return Jwts.parser()
	            .verifyWith(key)
	            .build()
	            .parseSignedClaims(token)
	            .getPayload();
	}

}
