package com.ingegneriadelsoftware.ProSki.Security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Questa classe definisce un filtro per la risoluzione delle eccezioni JWT.
 * Il filtro controlla se la richiesta contiene un token JWT scaduto e, in tal caso,
 * restituisce una risposta HTTP 401 (Unauthorized) con un messaggio di errore.
 * In caso contrario, la richiesta viene passata al filtro successivo.
 */
@Component
@RequiredArgsConstructor
public class JwtResolverException extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request, response);
        }catch (ExpiredJwtException e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token scaduto, rifare l'autenticazione");
        }
    }
}
