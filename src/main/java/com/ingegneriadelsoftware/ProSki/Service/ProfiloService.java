package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.DTO.Request.AuthenticationRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.RegistrazioneRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.AuthenticationResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Response.RegistrazioneResponse;
import com.ingegneriadelsoftware.ProSki.Model.Ruolo;
import com.ingegneriadelsoftware.ProSki.Model.Utente;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;


/**
 * Service che si occupa della registrazione, conferma e login di un utente
 */
@Service
@RequiredArgsConstructor
public class ProfiloService {

    /**
     * attributi che vengono iniettati nella classe per utilizzare i vari services
     */
    private final JwtService jwtService;
    private final UtenteService utenteSevice;
    private final AuthenticationManager authenticationManager;

    /**
     * Registra utente con email come identificativo e controlla l'eventuale presenza
     * @param request
     * @return RegistrazioneResponse contiene un attributo token di tipo String
     */
    public RegistrazioneResponse registrazione(RegistrazioneRequest request) throws IllegalStateException{
        String jwtToken;

        jwtToken = utenteSevice.iscrizione(
                new Utente(
                    request.getNome(),
                    request.getCognome(),
                    request.getPassword(),
                    request.getEmail(),
                    Ruolo.USER)
        );

        return RegistrazioneResponse.builder()
                .token(jwtToken)
                .build();
    }

    /**
     * Conferma la registrazione di un utente dopo che ha verificato tramite email, se il token è scaduto l'utente viene eliminato
     * @return String
     */
    public void confermaToken(String token) throws IllegalStateException {
        Utente utente = utenteSevice.findUtenteByToken(token);

        if(utente == null) throw new IllegalStateException("Utente non esiste");
        if(utente.isEnable()) throw new IllegalStateException("l'utente è gia registrato");

        try{
            jwtService.isTokenValid(token, utente);
        }catch(ExpiredJwtException e) {
            utenteSevice.deleteUtenteByEmail(utente.getEmail());
            throw new IllegalStateException("Token scaduto, registrazione fallita");
        }
        utenteSevice.abilitaUtente(utente.getEmail());
    }

    /**
     * Login di un utente attraverso jwt
     * @param request
     */
    public AuthenticationResponse authentication(AuthenticationRequest request) throws AuthenticationException {
        UserDetails user;

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        user = (UserDetails) auth.getPrincipal();

        String jwtToken = jwtService.generateToken(
                user,
                new Date(System.currentTimeMillis() + 1000 * 60 * 24)
        );

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
