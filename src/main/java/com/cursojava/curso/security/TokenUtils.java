package com.cursojava.curso.security;

import com.cursojava.curso.models.User;
import com.cursojava.curso.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class TokenUtils {
    private final static String ACCESS_TOKEN_SECRET = "mfkmf9s9fs9f39rjfmlfmlsfrutsmvkdmksmsfisfsm1kfsjksndviosaero2001";
    //La sesion durara 5 horas
    private final static Long ACCESS_TOKEN_VALIDITY_SECONDS = 18_000_000L;

    public static String createToken(String email, String username){
        long expirationTime = ACCESS_TOKEN_VALIDITY_SECONDS * 1_000;
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);

        Map<String, Object> extra = new HashMap<>();
        extra.put("email", email);
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(expirationDate)
                .addClaims(extra)
                .signWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes()))
                .compact();
    }

    public static UsernamePasswordAuthenticationToken getAuthentication(String token, UserRepository userRepository){
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(ACCESS_TOKEN_SECRET.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String username = claims.getSubject();
            User user = userRepository.findOneByUsername(username).orElseThrow(() -> new JwtException("User not found"));
            return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
        }
        catch (JwtException e) {
            return null;
        }
    }
}
