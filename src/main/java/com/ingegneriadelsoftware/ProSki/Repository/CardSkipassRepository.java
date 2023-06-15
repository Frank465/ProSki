package com.ingegneriadelsoftware.ProSki.Repository;

import com.ingegneriadelsoftware.ProSki.Model.CardSkipass;
import com.ingegneriadelsoftware.ProSki.Model.Location;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Repository
public interface CardSkipassRepository extends CrudRepository<CardSkipass, Integer> {

    Optional<CardSkipass> findByCardCode(String tessera);

}
