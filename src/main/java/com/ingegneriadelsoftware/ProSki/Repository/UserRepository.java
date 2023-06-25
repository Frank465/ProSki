package com.ingegneriadelsoftware.ProSki.Repository;

import com.ingegneriadelsoftware.ProSki.Model.Gender;
import com.ingegneriadelsoftware.ProSki.Model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    Optional<User> findUserByEmail(String email);

    List<User> findAllByEnable(boolean enable);

    @Transactional
    @Modifying
    @Query("UPDATE User a " +
            "SET a.enable = TRUE WHERE a.email = ?1")
    void enableUser(String email);

    Optional<User> findByToken(String token);

    List<User> findAllByGenderAndEnable(Gender gender, boolean enable);

    List<User> findAllByDateBirthBetweenAndEnable(LocalDate startAge, LocalDate endAge, boolean anable);
}
