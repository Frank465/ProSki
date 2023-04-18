package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.Email.BuildEmail;
import com.ingegneriadelsoftware.ProSki.Email.EmailSender;
import com.ingegneriadelsoftware.ProSki.Model.Utente;
import com.ingegneriadelsoftware.ProSki.Repository.UtenteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UtenteService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final UtenteRepository utenteRepository;
    private final JwtService jwtService;
    private final EmailSender emailSend;
    private final BuildEmail buildEmail;
    private final String UTENTE_NON_TROVATO_MSG = "utente con email %s non è stato trovato";

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return utenteRepository.findUserByEmail(email).
                orElseThrow(()-> new UsernameNotFoundException(String.format(UTENTE_NON_TROVATO_MSG, email)));
    }

    @Transactional
    public String iscrizione(Utente utente) throws IllegalStateException {
        Optional<Utente> utenteEsiste = utenteRepository.findUserByEmail(utente.getEmail());

        if(utenteEsiste.isPresent() && utenteEsiste.get().isEnable())
            throw new IllegalStateException("l'email inserita è già presente");

        utente.setPassword(passwordEncoder.encode(utente.getPassword()));

        String jwtToken = jwtService.generateToken(utente);

        String link = "http://localhost:8080/api/v1/profilo/confirm?token=" + jwtToken;
        emailSend.send(utente.getEmail(), buildEmail.create(utente.getNome(), link));

        utenteRepository.save(utente);

        return jwtToken;
    }

    public void abilitaUtente(String email) {
        utenteRepository.enableUtente(email);
    }

    public void deleteByEmail(String email) {
        utenteRepository.deleteUtenteByEmail(email);
    }
}
