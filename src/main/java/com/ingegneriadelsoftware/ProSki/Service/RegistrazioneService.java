package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.DTO.RegistrazioneRequest;
import com.ingegneriadelsoftware.ProSki.Email.BuildEmail;
import com.ingegneriadelsoftware.ProSki.Email.EmailSender;
import com.ingegneriadelsoftware.ProSki.Model.Ruolo;
import com.ingegneriadelsoftware.ProSki.Model.Token;
import com.ingegneriadelsoftware.ProSki.Model.Utente;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegistrazioneService {

    /**
     * attributi che vengono iniettati nella classe per utilizzare i vari services
     */
    private final TokenService tokenService;
    private final UtenteService utenteSevice;
    private final ValidazioneEmail validazioneEmail;
    private final EmailSender emailSend;
    private final BuildEmail buildEmail;

    /**
     * Registra utente con email come identificativo e controlla l'eventuale presenza
     * @param request
     * @return String, è il token che viene associato all'utente
     */
    public String registrazione(@NotNull RegistrazioneRequest request){
        boolean isValid = validazioneEmail.test(request.getEmail());
        if(!isValid)
            throw new IllegalStateException("l'email non è valida");

        String token = utenteSevice.iscrizione(new Utente(
                request.getNome(),
                request.getCognome(),
                request.getPassword(),
                request.getEmail(),
                Ruolo.USER));

        String link = "http://localhost:8080/api/v1/registrazione/confirm?token=" + token;
        emailSend.send(request.getEmail(), buildEmail.create(request.getNome(), link));

        return token;
    }

    /**
     * Conferma la registrazione di un utente dopo che ha verificato tramite email
     * @return conferma registrazione
     */
    @Transactional
    public String confermaToken(String token) {
        Token confirmToken  = tokenService.getToken(token)
                .orElseThrow(()-> new IllegalStateException("Token non trovato"));
        if(confirmToken.getConfirmedAt() != null)
            throw new IllegalStateException("la mail è già stata verificata");

        LocalDateTime expiredAt = confirmToken.getExpiresAt();

        if(expiredAt.isBefore(LocalDateTime.now())) {
            tokenService.deleteToken(token);
            throw new IllegalStateException("token scaduto");
        }
        tokenService.setConfirmedAt(token);
        utenteSevice.abilitaUtente(confirmToken.getUtente().getEmail());

        return "confirmed";
    }
}
