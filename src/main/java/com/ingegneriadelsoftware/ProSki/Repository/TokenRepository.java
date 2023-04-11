package com.ingegneriadelsoftware.ProSki.Repository;

import com.ingegneriadelsoftware.ProSki.Model.Token;
import com.ingegneriadelsoftware.ProSki.Model.Utente;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TokenRepository extends CrudRepository<Token, String> {

     Optional<Token> findTokenByTokenName(String token);

     @Transactional
     @Modifying
     @Query("UPDATE Token c " +
             "SET c.confirmedAt = ?2 " +
             "WHERE c.tokenName = ?1")
     int updateConfirmedAt(String token,
                           LocalDateTime confirmedAt);

     void deleteTokenByTokenName(String token);

    /**
     * Cerca il token dell'utente conoscendo il suo id
     * @param utente
     * @return
     */

    Optional<Token> findTokenByUtente(Utente utente);
}
