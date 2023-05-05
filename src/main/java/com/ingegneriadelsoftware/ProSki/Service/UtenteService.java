package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.Email.BuildEmail;
import com.ingegneriadelsoftware.ProSki.Email.EmailSender;
import com.ingegneriadelsoftware.ProSki.Model.Utente;
import com.ingegneriadelsoftware.ProSki.Repository.UtenteRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UtenteService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final UtenteRepository utenteRepository;
    private final JwtService jwtService;
    private final EmailSender emailSend;
    private final BuildEmail buildEmail;



    @Override
    public UserDetails loadUserByUsername(String email) throws IllegalStateException {
        return utenteRepository.findUserByEmail(email).
                orElseThrow(()-> new IllegalStateException("l'utente non è stato trovato"));
    }

    /**
     * Controllo se l'utente è già registrato.
     * Per ogni utente che si può registrare viene generato un token che ha validità 15 minuti.
     * Invio della mail per la registrazione all'indirizzo di posta elettronica.
     * @param utente
     * @return String
     * @throws IllegalStateException
     */
    public String iscrizione(Utente utente) throws IllegalStateException {
        Optional<Utente> utenteEsiste = utenteRepository.findUserByEmail(utente.getEmail());

        if(utenteEsiste.isPresent()) {
            if (utenteEsiste.get().isEnable())
                throw new IllegalStateException("l'utente esiste");
            try {
                jwtService.isTokenValid(utenteEsiste.get().getToken(), utenteEsiste.get());
            } catch (ExpiredJwtException e) {
                deleteUtenteByEmail(utenteEsiste.get().getEmail());
                throw new IllegalStateException("token precedente scaduto, registrare nuovamente l'utente");
            }
            throw new IllegalStateException("l'utente esiste");
        }

        utente.setPassword(passwordEncoder.encode(utente.getPassword()));

        String jwtToken = jwtService.generateToken(
                utente,
                new Date(System.currentTimeMillis() + 15 * 1000 * 60)
        );

        utente.setToken(jwtToken);

        String link = "http://localhost:8080/api/v1/profilo/confirm?token=" + jwtToken;
        emailSend.send(utente.getEmail(), buildEmail.create(utente.getNome(), link));

        utenteRepository.save(utente);
        return jwtToken;
    }

    public void abilitaUtente(String email) {
        utenteRepository.enableUtente(email);
    }

    public void deleteUtenteByEmail(String email) {
        utenteRepository.deleteByEmail(email);
    }

    public Utente findUtenteByToken(String token) {
        Optional<Utente> utente = utenteRepository.findByToken(token);
        if(utente.isPresent())
            return utente.get();
        return null;
    }

}
