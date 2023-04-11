package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.Model.Token;
import com.ingegneriadelsoftware.ProSki.Model.Utente;
import com.ingegneriadelsoftware.ProSki.Repository.UtenteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UtenteService implements UserDetailsService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UtenteRepository utenteRepository;
    private final String UTENTE_NON_TROVATO_MSG = "utente con email %s non è stato trovato";
    private final TokenService tokenService;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return utenteRepository.findUserByEmail(email). //TODO: bisogna controllare se è enable
                orElseThrow(()-> new UsernameNotFoundException(String.format(UTENTE_NON_TROVATO_MSG, email)));
    }

    @Transactional
    public String iscrizione(Utente utente) {
        Optional<Utente> utenteEsiste = utenteRepository.findUserByEmail(utente.getEmail());
        if(utenteEsiste.isPresent()) {
            Token tokenUtente = tokenService.findTokenByUtente(utenteEsiste.get());
            boolean tokenExpire = LocalDateTime.now().isAfter(tokenUtente.getExpiresAt());
            if(tokenExpire && !utenteEsiste.get().isEnable()) {
                tokenService.deleteToken(tokenUtente.getTokenName());
                utenteRepository.deleteUtenteByEmail(utenteEsiste.get().getEmail());
            }
            else
                throw new IllegalStateException("l'email inserita è già presente");
        }
        utente.setPassword(bCryptPasswordEncoder.encode(utente.getPassword()));
        utenteRepository.save(utente);

        String token = UUID.randomUUID().toString();
        Token confermaToken = new Token(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(10), utente);
        tokenService.confermaToken(confermaToken);
        return token;
    }

    public void abilitaUtente(String email) {
        utenteRepository.enableUtente(email);
    }

}
