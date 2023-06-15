package com.ingegneriadelsoftware.ProSki.Repository;

import com.ingegneriadelsoftware.ProSki.Model.Reservation;
import com.ingegneriadelsoftware.ProSki.Model.User;
import com.ingegneriadelsoftware.ProSki.Model.Vendor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, Integer> {

    List<Reservation> findAllByUser(User user);

    List<Reservation> findAllByVendor(Vendor vendor);
}
