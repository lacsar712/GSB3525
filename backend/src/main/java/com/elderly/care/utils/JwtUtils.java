package com.elderly.care.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtils {
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRE = 1000 * 60 * 60 * 24; // 24 hours

    public static String generateToken(String username, String role, Long userId) {
        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .claim("userId", userId)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(key)
                .compact();
    }

    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
