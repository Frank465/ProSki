package com.ingegneriadelsoftware.ProSki.Repository;

import com.ingegneriadelsoftware.ProSki.Model.LocationMessage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationMessageRepository extends CrudRepository<LocationMessage, Integer> {
}
