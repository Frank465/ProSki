package com.ingegneriadelsoftware.ProSki.Repository;

import com.ingegneriadelsoftware.ProSki.Model.Rifornitore;
import com.ingegneriadelsoftware.ProSki.Model.Sci;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SciRepository extends CrudRepository<Sci, Integer> {

    List<Sci> findByRifornitore(Rifornitore rifornitore);

    @Transactional
    @Modifying
    @Query("UPDATE Sci s  SET s.enable =?1  WHERE s.id = ?2")
    void setEnable(boolean enable, Integer idSci);

}
