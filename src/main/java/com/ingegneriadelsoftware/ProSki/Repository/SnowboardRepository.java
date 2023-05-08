package com.ingegneriadelsoftware.ProSki.Repository;

import com.ingegneriadelsoftware.ProSki.Model.Snowboard;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface SnowboardRepository extends CrudRepository<Snowboard, Integer> {
    @Query("select '*' from Snowboard where rifornitore = ?1")
    Set<Snowboard> findByIdRifornitore(Integer id);

    @Transactional
    @Modifying
    @Query("UPDATE Snowboard s SET s.enable =?2 WHERE s.id=?1")
    void setEnableById(Integer id, boolean value);

}
