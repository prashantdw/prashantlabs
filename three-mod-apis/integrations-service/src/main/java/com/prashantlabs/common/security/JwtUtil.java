package com.prashantlabs.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

public class JwtUtil {
  private final SecretKey key;
  private final String issuer;

  public JwtUtil(String secret, String issuer) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.issuer = issuer;
  }

  public String generate(String subject) {
    return Jwts.builder()
            .subject(subject)
            .issuer(issuer)
            .issuedAt(new Date())
            .expiration(Date.from(Instant.now().plusSeconds(3600)))
            .signWith(key)               // algorithm inferred from key
            .compact();
  }

  public Jws<Claims> validate(String token) {
    return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token);
  }
}