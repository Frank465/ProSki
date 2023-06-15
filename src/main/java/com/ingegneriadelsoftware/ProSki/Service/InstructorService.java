package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.DTO.Request.InstructorRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.MessageRequest;
import com.ingegneriadelsoftware.ProSki.ForumStrategy.ConcreteStrategyInstructor;
import com.ingegneriadelsoftware.ProSki.ForumStrategy.Context;
import com.ingegneriadelsoftware.ProSki.Model.*;
import com.ingegneriadelsoftware.ProSki.Repository.InstructorMessageRepository;
import com.ingegneriadelsoftware.ProSki.Repository.InstructorRepository;
import com.ingegneriadelsoftware.ProSki.Repository.LessonRepository;
import com.ingegneriadelsoftware.ProSki.Repository.UserRepository;
import com.ingegneriadelsoftware.ProSki.Security.JwtUtils;
import com.ingegneriadelsoftware.ProSki.Utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InstructorService {

    private final InstructorRepository instructorRepository;
    private final LocationService locationService;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final LessonRepository lessonRepository;
    private final Context context;
    private final InstructorMessageRepository instructorMessageRepository;

    public Instructor getInstructorByEmail(String email) throws EntityNotFoundException {
        return instructorRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Il maestro non esiste"));
    }

    /**
     * Creazione di un maestro e attribuzione ad una località esistente
     * @param request
     * @return
     * @throws IllegalStateException
     */
    public String insertInstructor(InstructorRequest request) throws EntityNotFoundException{
        Optional <Instructor> maestro = instructorRepository.findByEmail(request.getEmail());
        if(maestro.isPresent()) throw new IllegalStateException("Il maestro è già stato inserito");
        Location location = locationService.getLocalitaByName(request.getLocation());
        Instructor newInstructor = new Instructor(request.getName(), request.getSurname(), request.getEmail(), request.getSpeciality(), location);
        instructorRepository.save(newInstructor);
        return "maestro "+ newInstructor.getName()+" aggiunto con successo";
    }

    public String createMessage(MessageRequest request, HttpServletRequest httpServletRequest) {
        //Controllo ed estrapolazione utente dal Context di Security
        User user = Utils.getUserFromHeader(httpServletRequest, userRepository, jwtUtils);
        //Controllo esistenza maestro e sue lezioni
        Instructor instructor = getInstructorByEmail(request.getUsername());
        List<Lesson> lessonsInstructor = lessonRepository.findAllByInstructor(instructor);
        if(lessonsInstructor.isEmpty()) throw new IllegalStateException("Il maestro non ha pubblicato lezioni");
        //Controllo se l'utente si è mai iscritto ad una lezione del maestro
        boolean isPresent = false;
        for (Lesson lesson : lessonsInstructor) {
            if(lesson.getUsers().contains(user)) {
                isPresent = true;
                break;
            }
        }
        if(!isPresent) throw new IllegalStateException("L'utente non ha svolto una lezione con il maestro " + instructor.getName());

        context.setPublishingStrategy(new ConcreteStrategyInstructor(instructorRepository, instructorMessageRepository));
        return context.executeStrategy(instructor.getEmail(), user, request.getMessage());
    }
}
