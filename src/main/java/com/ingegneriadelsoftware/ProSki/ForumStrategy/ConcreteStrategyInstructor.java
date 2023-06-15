package com.ingegneriadelsoftware.ProSki.ForumStrategy;

import com.ingegneriadelsoftware.ProSki.Model.*;
import com.ingegneriadelsoftware.ProSki.Repository.InstructorMessageRepository;
import com.ingegneriadelsoftware.ProSki.Repository.InstructorRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class ConcreteStrategyInstructor implements PublishingStrategy{


    private InstructorRepository instructorRepository;
    private InstructorMessageRepository instructorMessageRepository;

    public ConcreteStrategyInstructor(InstructorRepository instructorRepository, InstructorMessageRepository instructorMessageRepository) {
        this.instructorRepository = instructorRepository;
        this.instructorMessageRepository = instructorMessageRepository;
    }

    @Override
    public String publishingMessage(String emailInstructor, User user, String message) {
        Optional<Instructor> instructor = instructorRepository.findByEmail(emailInstructor);
        InstructorMessage instructorMessage = new InstructorMessage(instructor.get(), user, message);
        instructorMessageRepository.save(instructorMessage);
        return instructorMessage.getMessage();
    }
}
