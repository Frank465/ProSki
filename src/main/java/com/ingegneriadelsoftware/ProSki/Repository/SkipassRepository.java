package com.ingegneriadelsoftware.ProSki.Repository;

import com.ingegneriadelsoftware.ProSki.Model.CardSkipass;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkipassRepository extends CrudRepository<CardSkipass, Integer> {

    Optional<CardSkipass> findByCardCode(String tessera);
}
