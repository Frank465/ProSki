package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.DTO.AuthenticationRequest;
import com.ingegneriadelsoftware.ProSki.DTO.AuthenticationResponse;
import com.ingegneriadelsoftware.ProSki.DTO.RegistrazioneRequest;
import com.ingegneriadelsoftware.ProSki.DTO.RegistrazioneResponse;
import com.ingegneriadelsoftware.ProSki.Email.BuildEmail;
import com.ingegneriadelsoftware.ProSki.Email.EmailSender;
import com.ingegneriadelsoftware.ProSki.Model.Ruolo;
import com.ingegneriadelsoftware.ProSki.Model.Utente;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfiloService {

    /**
     * attributi che vengono iniettati nella classe per utilizzare i vari services
     */
    private final JwtService jwtService;
    private final UtenteService utenteSevice;
    private final ValidazioneEmail validazioneEmail;
    private final EmailSender emailSend;
    private final BuildEmail buildEmail;
    private final AuthenticationManager authenticationManager;

    /**
     * Registra utente con email come identificativo e controlla l'eventuale presenza
     * @param request
     * @return String, è il token che viene associato all'utente
     */
    public RegistrazioneResponse registrazione(@NotNull RegistrazioneRequest request){
        boolean isValid = validazioneEmail.test(request.getEmail());
        if(!isValid)
            throw new IllegalStateException("l'email non è valida");

        Utente utente = utenteSevice.iscrizione(new Utente(
                request.getNome(),
                request.getCognome(),
                request.getPassword(),
                request.getEmail(),
                Ruolo.USER));

        String jwtToken = jwtService.generateToken(utente);

        String link = "http://localhost:8080/api/v1/registrazione/confirm?token=" + jwtToken;
        emailSend.send(request.getEmail(), buildEmail.create(request.getNome(), link));

        return RegistrazioneResponse.builder()
                .token(jwtToken)
                .build();
    }

    /**
     * Conferma la registrazione di un utente dopo che ha verificato tramite email
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
            throw new ExpiredJwtException(jwtService.exctractHeader(token),
                    jwtService.extractAllClaims(token),
                    "Registrare di nuovo l'utente"
            );
        }
    }

    public AuthenticationResponse authentication(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        UserDetails user = utenteSevice.loadUserByUsername(request.getEmail());

        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
