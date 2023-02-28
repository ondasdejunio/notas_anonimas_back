package com.cursojava.curso.security;

import com.cursojava.curso.dtos.LoggedUserDataDTO;
import com.cursojava.curso.models.User;
import com.cursojava.curso.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        AuthCredentials authCredentials = new AuthCredentials();
    try {
        authCredentials = new ObjectMapper().readValue(request.getReader(), AuthCredentials.class);
    }
    catch (IOException e) {
    }
        UsernamePasswordAuthenticationToken usernamePAT = new UsernamePasswordAuthenticationToken(
                authCredentials.getUsername(),
                authCredentials.getPassword(),
                Collections.emptyList()
        );
        try {
            Authentication authentication = getAuthenticationManager().authenticate(usernamePAT);
            return authentication;
        } catch (BadCredentialsException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                objectMapper.writeValue(response.getOutputStream(), "Credenciales inválidas");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        UserDetailsImp userDetails = (UserDetailsImp) authResult.getPrincipal();
        LoggedUserDataDTO loggedUserDataDTO = new LoggedUserDataDTO(userDetails.getUsername(), userDetails.getEmail(), userDetails.getDateBirth(), userDetails.getGender());
        String token = TokenUtils.createToken(userDetails.getEmail(), userDetails.getUsername());
        response.setContentType("application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("access_token", token);
        tokenMap.put("user", loggedUserDataDTO);

        try {
            objectMapper.writeValue(response.getOutputStream(), tokenMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        super.successfulAuthentication(request, response, chain, authResult);
    }
}
