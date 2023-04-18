package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.DTO.AuthenticationRequest;
import com.ingegneriadelsoftware.ProSki.DTO.AuthenticationResponse;
import com.ingegneriadelsoftware.ProSki.DTO.RegistrazioneRequest;
import com.ingegneriadelsoftware.ProSki.DTO.RegistrazioneResponse;
import com.ingegneriadelsoftware.ProSki.Model.Ruolo;
import com.ingegneriadelsoftware.ProSki.Model.Utente;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


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
    private final ValidazioneEmail validazioneEmail;
    private final AuthenticationManager authenticationManager;

    /**
     * Registra utente con email come identificativo e controlla l'eventuale presenza
     * @param request
     * @return RegistrazioneResponse contiene un attributo token di tipo String
     */
    public RegistrazioneResponse registrazione(@NotNull RegistrazioneRequest request){

        String jwtToken;
        try {
            validazioneEmail.test(request.getEmail());
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
            throw new ExpiredJwtException(
                    jwtService.exctractHeader(token),
                    jwtService.extractAllClaims(token),
                    "Registrare di nuovo l'utente"
            );
        }
    }

    /**
     * Login di un utente attraverso jwt
     *
     * @param request
     */
    public AuthenticationResponse authentication(AuthenticationRequest request) {
        UserDetails user = null;
        try {
             user = utenteSevice.loadUserByUsername(request.getEmail());
        }catch (UsernameNotFoundException unfe) {
           return AuthenticationResponse.builder()
                   .message(unfe.getMessage())
                   .build();
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .message(null)
                .build();
    }
}
