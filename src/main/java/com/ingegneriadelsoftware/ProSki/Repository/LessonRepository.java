package com.ingegneriadelsoftware.ProSki.Repository;

import com.ingegneriadelsoftware.ProSki.Model.Lesson;
import com.ingegneriadelsoftware.ProSki.Model.Instructor;
import com.ingegneriadelsoftware.ProSki.Model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LessonRepository extends CrudRepository<Lesson, Integer> {
    List<Lesson> findAllByInstructor(Instructor instructor);

    List<Lesson> findAll();

}
