package com.ingegneriadelsoftware.ProSki.ForumStrategy;

import com.ingegneriadelsoftware.ProSki.Model.*;
import com.ingegneriadelsoftware.ProSki.Repository.InstructorCommentRepository;
import com.ingegneriadelsoftware.ProSki.Repository.InstructorMessageRepository;
import com.ingegneriadelsoftware.ProSki.Repository.InstructorRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Component
public class ConcreteStrategyInstructor implements PublishingStrategy{


    private InstructorRepository instructorRepository;
    private InstructorMessageRepository instructorMessageRepository;
    private InstructorCommentRepository instructorCommentRepository;

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

    @Override
    public String publishComment(Integer idInstructorMessenge, User user, String message) {
        Optional<InstructorMessage> instructorMessage = instructorMessageRepository.findById(idInstructorMessenge);
        InstructorComment instructorComment = new InstructorComment(user, instructorMessage.get(), message);
        instructorCommentRepository.save(instructorComment);
        return message;
    }
}
