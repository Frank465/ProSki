package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.Model.Token;
import com.ingegneriadelsoftware.ProSki.Model.Utente;
import com.ingegneriadelsoftware.ProSki.Repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;


    /**
     * il token creato viene aggiunto nel DB, ma non convalidato
     * @param token
     */
    public void confermaToken(Token token) {
        tokenRepository.save(token);
    }

    /**
     *Ritorna l'oggetto token con i suoi attributi
     * @param token
     * @return Token
     */
    public Optional<Token> getToken(String token) {
        return tokenRepository.findTokenByTokenName(token);
    }

    /**
     * Convalida il token dopo che l'utente ha confermato la registrazione tramite email
     * @param token
     */
    public void setConfirmedAt(String token) {
        tokenRepository.updateConfirmedAt(
                token, LocalDateTime.now());
    }

    public Token findTokenByUtente(Utente utente) {
        Optional<Token> token = tokenRepository.findTokenByUtente(utente);
        if(token.isEmpty())
            throw new IllegalStateException("Token non trovato");
        return token.get();
    }

    public void deleteToken(String token) {
        tokenRepository.deleteTokenByTokenName(token);
    }
}
