package com.ingegneriadelsoftware.ProSki.Repository;

import com.ingegneriadelsoftware.ProSki.Model.BuySkipass;
import com.ingegneriadelsoftware.ProSki.Model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuySkipassRepository extends CrudRepository<BuySkipass, Integer> {

    List<BuySkipass> findAllByUser(User user);
}
