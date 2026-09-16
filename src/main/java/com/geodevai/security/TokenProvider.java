package com.geodevai.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import com.geodevai.data.model.User;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class TokenProvider {
    @Value("${geodevai.jwt.secret}")
    private String jwtSecret;

    @Value("${geodevai.jwt.expiration-time}")
    @Getter
    private Long jwtExpirationMinutes;

    public Instant getExpirationTime() {
        return ZonedDateTime.now().plusMinutes(jwtExpirationMinutes).toInstant();
    }

    public String generate(User user) {
        byte[] signingKey = jwtSecret.getBytes();

        return Jwts.builder()
                .header().add("typ", TOKEN_TYPE)
                .and()
                .signWith(Keys.hmacShaKeyFor(signingKey), Jwts.SIG.HS512)
                .expiration(Date.from(getExpirationTime()))
                .issuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .id(UUID.randomUUID().toString())
                .issuer(TOKEN_ISSUER)
                .audience().add(TOKEN_AUDIENCE)
                .and()
                .subject(user.getUserId().toString())
                .claim("role", user.getRole())
                .claim("displayName", user.getDisplayName())
                .claim("email", user.getContactEmail())
                .compact();
    }

    public Optional<Jws<Claims>> validateTokenAndGetJws(String token) {
        try {
            byte[] signingKey = jwtSecret.getBytes();

            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(signingKey))
                    .build()
                    .parseSignedClaims(token);

            return Optional.of(jws);
        } catch (ExpiredJwtException exception) {
            log.error("Request to parse expired JWT : {} failed : {}", token, exception.getMessage());
        } catch (UnsupportedJwtException exception) {
            log.error("Request to parse unsupported JWT : {} failed : {}", token, exception.getMessage());
        } catch (MalformedJwtException exception) {
            log.error("Request to parse invalid JWT : {} failed : {}", token, exception.getMessage());
        } catch (SignatureException exception) {
            log.error("Request to parse JWT with invalid signature : {} failed : {}", token, exception.getMessage());
        } catch (IllegalArgumentException exception) {
            log.error("Request to parse empty or null JWT : {} failed : {}", token, exception.getMessage());
        }
        return Optional.empty();
    }

    public static final String TOKEN_TYPE = "JWT";
    public static final String TOKEN_ISSUER = "geodevai-api";
    public static final String TOKEN_AUDIENCE = "geodevai-app";
}