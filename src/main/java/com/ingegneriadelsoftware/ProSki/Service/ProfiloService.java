package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.DTO.AuthenticationRequest;
import com.ingegneriadelsoftware.ProSki.DTO.AuthenticationResponse;
import com.ingegneriadelsoftware.ProSki.DTO.RegistrazioneRequest;
import com.ingegneriadelsoftware.ProSki.DTO.RegistrazioneResponse;
import com.ingegneriadelsoftware.ProSki.Model.Ruolo;
import com.ingegneriadelsoftware.ProSki.Model.Utente;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    public RegistrazioneResponse registrazione(RegistrazioneRequest request){

        String jwtToken;
        try {
            jwtToken = utenteSevice.iscrizione(new Utente(
                    request.getNome(),
                    request.getCognome(),
                    request.getPassword(),
                    request.getEmail(),
                    Ruolo.USER));
        }catch(Exception e){
            return RegistrazioneResponse.builder()
                    .message(e.getMessage())
                    .build();
        }

        return RegistrazioneResponse.builder()
                .token(jwtToken)
                .build();
    }

    /**
     * Conferma la registrazione di un utente dopo che ha verificato tramite email, se il token è scaduto l'utente viene eliminato
     * @return String
     */
    public String confermaToken(String token) {
        boolean isExpired = jwtService.isTokenExpired(token);
        String userEmail = jwtService.exctractUsername(token);

        if(!isExpired) {
            utenteSevice.abilitaUtente(userEmail);
            return "confirmed";
        }
        else {
            utenteSevice.deleteByEmail(userEmail);
            return "Token scaduto registrare di nuovo l'utente";
        }
    }

    /**
     * Login di un utente attraverso jwt
     *
     * @param request
     */
    public AuthenticationResponse authentication(AuthenticationRequest request) {
        UserDetails user;

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        }catch (AuthenticationException e) {
            return AuthenticationResponse.builder()
                    .token(null)
                    .message(e.getMessage())
                    .build();
        }

        user = utenteSevice.loadUserByUsername(request.getEmail());

        String jwtToken = jwtService.generateToken(user,
                new Date(System.currentTimeMillis() + 1000 * 60 * 24)
        );
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .message(null)
                .build();
    }
}
