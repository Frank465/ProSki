package com.ingegneriadelsoftware.ProSki.Repository;

import com.ingegneriadelsoftware.ProSki.Model.Instructor;
import com.ingegneriadelsoftware.ProSki.Model.InstructorMessage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstructorMessageRepository extends CrudRepository<InstructorMessage, Integer> {

    List<InstructorMessage> findAllByInstructor(Instructor instructor);

}
