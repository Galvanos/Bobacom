package com.bobacom.backend.security.interfaces;

import org.springframework.security.core.Authentication;

public interface JwtService {
	String generateAccessToken(Authentication authentication);
	String generateRefreshToken(Authentication authentication);
	boolean isValidRefreshToken(String token) throws Exception;
	String extractUsername(String token);
}
