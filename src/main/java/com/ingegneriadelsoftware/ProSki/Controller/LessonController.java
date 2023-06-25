package com.ingegneriadelsoftware.ProSki.Controller;

import com.ingegneriadelsoftware.ProSki.DTO.DTOManager;
import com.ingegneriadelsoftware.ProSki.DTO.Request.LessonRequest;
import com.ingegneriadelsoftware.ProSki.Service.LessonService;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;

@RestController
@RequestMapping("/api/v1/lesson")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    /**
     * L'endpoint permette all'admin di creare una lezione passando una LessonRequest(DTO) che ne contiene le informazioni
     * l'oggetto di risposta del service createLesson() ritorna la lezione che è stata generata e che viene in primis Mappata
     * a DTO e poi inserito nella ResponseEntity. Il metodo può sollevare eccezioni nel caso di errore della richiesta o
     * incosistenza dei dati richiesti
     * @param request
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createLesson(@Valid @RequestBody LessonRequest request) {
        try{
            return ResponseEntity.ok(DTOManager.toLessonResponseByLesson(lessonService.createLesson(request)));
        } catch (IllegalStateException | EntityNotFoundException | DateTimeException | MessagingException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * L'endPoint è accessibile a tutti e ritorna la lista di lezioni di tutti i maestri (che sono  anche in diverse località)
     * Solleva eccezione se non esistono lezioni dopo la data odierna
     * @return
     */
    @GetMapping("/getAll")
    public ResponseEntity<?> getLessons() {
        try{
            return ResponseEntity.ok(lessonService.getListLessons());
        }catch(EntityNotFoundException ex) {
            return new ResponseEntity<>("Non ci sono lezioni disponibili", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * L'endPoint permette di visualizzare tutti i messaggi di un istruttore passando nella url l'id il suo id.
     * La ResponseEntity è formata da una lista di lezioni che hanno data successiva a quella odierna mentre nel casa di
     * errori contiene il messaggio d'errore più la status indicato
     * @param idInstructor
     * @return
     */
    @GetMapping("/getAll/byInstructor/{id_instructor}")
    public ResponseEntity<?> getLessonsByInstructor(@PathVariable("id_instructor") Integer idInstructor) {
        try{
            return ResponseEntity.ok(lessonService.getListLessonsByInstructor(idInstructor));
        }catch(EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * L'endPoint permette di visualizzare tutti i messaggi di una location passando nella url l'id il suo id.
     * La ResponseEntity è formata da una lista di lezioni che hanno data successiva a quella odierna mentre nel casa di
     * errori contiene il messaggio d'errore più la status indicato
     * @param idLocation
     * @return
     */
    @GetMapping("/getAll/byLocation/{id_location}")
    public ResponseEntity<?> getLessonsByLocation(@PathVariable("id_location") Integer idLocation) {
        try{
            return ResponseEntity.ok(lessonService.getListLessonsByLocation(idLocation));
        }catch(EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
