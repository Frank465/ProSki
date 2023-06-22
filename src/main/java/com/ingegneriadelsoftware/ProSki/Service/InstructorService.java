package com.ingegneriadelsoftware.ProSki.Service;

import com.ingegneriadelsoftware.ProSki.DTO.DTOManager;
import com.ingegneriadelsoftware.ProSki.DTO.Request.CommentRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.InstructorRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.MessageRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.MessageResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Utils.CommentDTO;
import com.ingegneriadelsoftware.ProSki.DTO.Utils.MessageDTO;
import com.ingegneriadelsoftware.ProSki.ForumStrategy.ConcreteStrategyInstructor;
import com.ingegneriadelsoftware.ProSki.ForumStrategy.ConcreteStrategyLocation;
import com.ingegneriadelsoftware.ProSki.ForumStrategy.Context;
import com.ingegneriadelsoftware.ProSki.Model.*;
import com.ingegneriadelsoftware.ProSki.Repository.*;
import com.ingegneriadelsoftware.ProSki.Security.JwtUtils;
import com.ingegneriadelsoftware.ProSki.Utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private final InstructorCommentRepository instructorCommentRepository;

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
        Optional <Instructor> instructor = instructorRepository.findByEmail(request.getEmail());
        if(instructor.isPresent()) throw new IllegalStateException("Il maestro è già stato inserito");
        Location location = locationService.getLocalitaByName(request.getLocation());
        Instructor newInstructor = new Instructor(request.getName(), request.getSurname(), request.getEmail(), request.getSpeciality(), location);
        instructorRepository.save(newInstructor);
        return "maestro "+ newInstructor.getName()+" aggiunto con successo";
    }

    /**
     * Un utente può pubblicare un messaggio per un determinato maestro solo se si è iscritto almeno una volta
     * ad una sua lezione. Implementazione avviene tramite Strategy Method
     * @param request
     * @param httpServletRequest
     * @return
     */
    public String createMessage(MessageRequest request, HttpServletRequest httpServletRequest) {
        //Controllo ed estrapolazione utente dal Context di Security
        User user = Utils.getUserFromHeader(httpServletRequest, userRepository, jwtUtils);
        controlUserInstructor(request.getUsername(), user);
        //Set della strategia di publicazione del messaggio
        context.setPublishingStrategy(new ConcreteStrategyInstructor(instructorRepository, instructorMessageRepository));
        //Eseguo la strategia di pubblicazione del messaggio per il maestro
        return context.executeMessageStrategy(request.getUsername(), user, request.getMessage());
    }

    /**
     * Un utente scrive un commento su un messeggio esistente se l'utente rispecchia ha fatto una lezione
     * col maestro indicato
     * La creazione dei comment avviene tramite l'utilizzo del pattern Strategy
     * @param request
     * @param httpServletRequest
     * @return
     */
    public String createCommentToMessage(CommentRequest request, HttpServletRequest httpServletRequest) {
        //Controllo ed estrapolazione utente dal Context di Security
        User user = Utils.getUserFromHeader(httpServletRequest, userRepository, jwtUtils);
        //Controllo che l'utente abbia effettuato una prenotazione dal rifornitore
        controlUserInstructor(request.getUsername(), user);
        InstructorMessage instructorMessage = instructorMessageRepository.findById(request.getIdMessage()).orElseThrow(()-> new IllegalStateException("Il messaggio indicato non esiste"));
        //Set della strategia di publicazione del commento
        context.setPublishingStrategy(new ConcreteStrategyInstructor(instructorRepository, instructorMessageRepository, instructorCommentRepository));
        //Eseguo la strategia di pubblicazione del messaggio per il rifornitore
        return context.executeCommentStrategy(request.getIdMessage(), user, request.getComment());
    }

    /**
     * Controlla che l'utente che vuole inserire un messaggio o un commento abbia almeno una volta fatto una
     * lezione col maestro indicato
     * @param username
     * @param user
     */
    private void controlUserInstructor(String username, User user) {
        //Controllo esistenza maestro e sue lezioni
        Instructor instructor = getInstructorByEmail(username);
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
    }

    /**
     * Dato un maestro vengono ritornati tutti i messaggi che gli utenti hanno pubblicato e i rispettivi commenti
     * @param idMaestro
     * @return
     */
    public MessageResponse getAllMessage(Integer idMaestro) {
        Instructor instructor = instructorRepository.findById(idMaestro).orElseThrow(()-> new IllegalStateException("Il maestro non esiste"));
        List<InstructorMessage> instructorsMessage = instructorMessageRepository.findAllByInstructor(instructor);
        List<MessageDTO> messageDTOS = new ArrayList<>();
        //Crea una commentDTO nella quale ci sono i dati importanti di tutti i commenti legati ad un messaggio
        //Prende tutti i messaggi di una località (che vengono gestiti come DTO) e vengono aggiunti i relativi commenti per ogni messaggio
        instructorsMessage.forEach(cur -> {
            List<CommentDTO> commentDTOS = new ArrayList<>();
            cur.getInstructorComments().forEach(elem -> {
                commentDTOS.add(CommentDTO.builder().id(elem.getCommentId()).user(elem.getUser().getEmail()).comment(elem.getComment()).build());
            } );
            messageDTOS.add(MessageDTO.builder().idMessage(cur.getMessageId()).user(cur.getUser().getUserId()).message(cur.getMessage()).comments(commentDTOS).build());
        });
        return MessageResponse.builder().idLocation(idMaestro).listMessage(messageDTOS).build();
    }

    /**
     *Il metodo elimina un messaggio e a cascata tutti i commenti che ad esso si riferiscono.
     * In questo caso si tratta di un messaggio creato da un utente sul forum di un maestro
     * Il compito è riservato all'admin del sistema
     * @param request
     * @return
     */
    public MessageDTO deleteMessage(MessageDTO request) {
        InstructorMessage message = instructorMessageRepository.findById(request.getIdMessage())
                .orElseThrow(()->new EntityNotFoundException("Il messaggio cercato non esiste"));
        instructorMessageRepository.delete(message);
        return MessageDTO.builder()
                .idMessage(message.getMessageId())
                .message(message.getMessage())
                .user(message.getUser().getUserId())
                .build();
    }

    /**
     * Il metodo elimina una lista di commenti ritenuti in opportuni da parte dell'admin per un determinato messaggio
     * In questo caso si tratta di uno o più commenti che degli utenti fanno sul messaggio di un utente sul forum di un maestro
     * @param request
     * @return
     */
    public MessageDTO deleteComments(MessageDTO request) {
        if(request.getComments().isEmpty()) throw new IllegalStateException("Non ci sono commenti selezionati");
        InstructorMessage message = instructorMessageRepository.findById(request.getIdMessage())
                .orElseThrow(()-> new EntityNotFoundException("Il messaggio indicato non esiste"));
        List<InstructorComment> comments = new ArrayList<>();
        request.getComments().forEach(cur -> comments.add(instructorCommentRepository.findById(cur.getId())
                .orElseThrow(()-> new EntityNotFoundException("Un dei commenti indicati non esiste"))));
        instructorCommentRepository.deleteAll(comments);
        return MessageDTO.builder()
                .idMessage(message.getMessageId())
                .comments(request.getComments())
                .build();
    }
}
