package com.arul.complaint_management.security;

import java.nio.charset.StandardCharsets;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	  // Must be at least 32 bytes for HS256
    private static final String SECRET_KEY = "mySuperSecretKeyForJwtAuthentication123456789";

	
	//24 hours
    private static final long EXPIRATION_TIME = 1000*60*60*24;
    
    //create secret key
    private SecretKey getSignKey() {
    return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    } 
    
    // Generate JWT
    public String generateToken(String email) {

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignKey())
                .compact();
    }
    
    // Extract user name / email
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Extract all claims
    public Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    // Check expiry 
    public boolean isTokenExpired(String token) {

        return extractClaims(token)
                .getExpiration()
                .before(new Date());
    }
    
    // Validate token
    public boolean validateToken(String token, String email) {

        try {
            return extractUsername(token).equals(email)
                    && !isTokenExpired(token);

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
}
