package com.ingegneriadelsoftware.ProSki.Repository;

import com.ingegneriadelsoftware.ProSki.Model.Lezione;
import com.ingegneriadelsoftware.ProSki.Model.Utente;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtenteRepository extends CrudRepository<Utente, Integer> {

    Optional<Utente> findUserByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE Utente a " +
            "SET a.enable = TRUE WHERE a.email = ?1")
    int enableUtente(String email);

    @Transactional
    @Modifying
    @Query("DELETE FROM Utente a WHERE a.email = ?1")
    void deleteByEmail(String email);

    Optional<Utente> findByToken(String token);
}
