package com.ingegneriadelsoftware.ProSki.Repository;

import com.ingegneriadelsoftware.ProSki.Model.Vendor;
import com.ingegneriadelsoftware.ProSki.Model.Sky;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkyRepository extends CrudRepository<Sky, Integer> {

    List<Sky> findByVendor(Vendor vendor);

    @Transactional
    @Modifying
    @Query("UPDATE Sky s  SET s.enable =?1  WHERE s.id = ?2")
    void setEnable(boolean enable, Integer idSky);

}
