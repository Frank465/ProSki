package com.ingegneriadelsoftware.ProSki.Repository;

import com.ingegneriadelsoftware.ProSki.Model.Vendor;
import com.ingegneriadelsoftware.ProSki.Model.Snowboard;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SnowboardRepository extends CrudRepository<Snowboard, Integer> {

    List<Snowboard> findByVendor(Vendor vendor);

    @Transactional
    @Modifying
    @Query("UPDATE Snowboard s SET s.enable =?2 WHERE s.id=?1")
    void setEnable(Integer id, boolean enable);

}
