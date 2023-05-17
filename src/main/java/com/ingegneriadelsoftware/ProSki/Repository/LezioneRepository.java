package com.ingegneriadelsoftware.ProSki.Repository;

import com.ingegneriadelsoftware.ProSki.Model.Lezione;
import com.ingegneriadelsoftware.ProSki.Model.Maestro;
import com.ingegneriadelsoftware.ProSki.Model.Utente;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LezioneRepository extends CrudRepository<Lezione, Integer> {
    List<Lezione> findAllByMaestro(Maestro maestro);

    List<Lezione> findAll();

}
