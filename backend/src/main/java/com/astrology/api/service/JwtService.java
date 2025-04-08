package com.astrology.api.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username) {
        return JwtTokenBuilder.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .build();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private static class JwtTokenBuilder {
        private String subject;
        private Date issuedAt;
        private Date expiration;
        private Key signingKey;
        private SignatureAlgorithm signatureAlgorithm;

        private JwtTokenBuilder() {}

        public static JwtTokenBuilder builder() {
            return new JwtTokenBuilder();
        }

        public JwtTokenBuilder setSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public JwtTokenBuilder setIssuedAt(Date issuedAt) {
            this.issuedAt = issuedAt;
            return this;
        }

        public JwtTokenBuilder setExpiration(Date expiration) {
            this.expiration = expiration;
            return this;
        }

        public JwtTokenBuilder signWith(Key signingKey, SignatureAlgorithm signatureAlgorithm) {
            this.signingKey = signingKey;
            this.signatureAlgorithm = signatureAlgorithm;
            return this;
        }

        public String build() {
            return Jwts.builder()
                    .setSubject(subject)
                    .setIssuedAt(issuedAt)
                    .setExpiration(expiration)
                    .signWith(signingKey, signatureAlgorithm)
                    .compact();
        }
    }
} 