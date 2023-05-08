package com.ingegneriadelsoftware.ProSki.Repository;

import com.ingegneriadelsoftware.ProSki.Model.Sci;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface SciRepository extends CrudRepository<Sci, Integer> {

    @Query("select '*' from Sci where rifornitore = ?1")
    Set<Sci> findByIdRifornitore(Integer id);

    @Transactional
    @Modifying
    @Query("UPDATE Sci s SET s.enable =?2 WHERE s.id=?1")
    void setEnableById(Integer id, boolean value );

}
