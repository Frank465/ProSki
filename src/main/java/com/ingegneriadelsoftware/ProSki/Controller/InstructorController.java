package com.ingegneriadelsoftware.ProSki.Controller;

import com.ingegneriadelsoftware.ProSki.DTO.Request.CommentRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.InstructorRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.MessageRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.MessageResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Utils.MessageDTO;
import com.ingegneriadelsoftware.ProSki.Service.InstructorService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * Questi sono i metodi che vengono chiamati quando bisogna interagire con funzionalità che riguardano il rifornitore
 */
@RestController
@RequestMapping("/api/v1/instructor")
@RequiredArgsConstructor
public class InstructorController {

    private final InstructorService instructorService;

    /**
     * L'endpoint crea un istruttore a partire da una request formulata come un DTO, solleva eccezioni se i campi che sono stati inseriti
     * non sono validi o se è già presente quel rifornitore che si vuole inserire.
     * Il metodo è accessibile solo a l'admin
     * @param request
     * @return
     */

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("create")
    public ResponseEntity<String> createInstructor(@Valid @RequestBody InstructorRequest request) {
        try {
            return ResponseEntity.ok(instructorService.insertInstructor(request));
        }catch (IllegalStateException | EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Questo endPoint viene chiamato ogni volta che si vuole creare un messaggio nel form di un riforniore.
     * Accetta in ingresso una request composta come un DTO prende httprequest necessario per capire chi è l'utente che sta
     * scrivendo il messaggio. Solleva eccezioni nel caso di mancanza di informazioni o dati passanti non esistenti nel db
     * @param request
     * @param httpRequest
     * @return
     */
    @PostMapping("create/message")
    public ResponseEntity<?> createMessageToInstructor(@Valid @RequestBody MessageRequest request, HttpServletRequest httpRequest) {
        try {
            return ResponseEntity.ok(instructorService.createMessage(request, httpRequest));
        } catch(EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * Questo endPoint come il precedente si comporta allo stesso modo. Solo che riguarda la scrittura da parte di un utente
     * di un commento ad un messaggio che è già stato pubblicato sul forum
     * @param request
     * @param httpRequest
     * @return
     */
    @PostMapping("/create/comment")
    public ResponseEntity<?> createCommentToInstructorMessage(@Valid @RequestBody CommentRequest request, HttpServletRequest httpRequest) {
        try {
            return ResponseEntity.ok(instructorService.createCommentToMessage(request, httpRequest));
        } catch(EntityNotFoundException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * L'endPoint serve per ritornare tutti i messaggi e i commenti che sono stati scritti per un determinato rifornitore
     * Utilizzato dagli utenti per poter interaggire e dall'admin per poter controllare che non ci siano messaggi indiscreti
     * @param idLocation
     * @return
     */
    @GetMapping("/get/all/message/{id_instructor}")
    public ResponseEntity<MessageResponse> getAllMessageByInstructor(@PathVariable("id_instructor") Integer idLocation) {
        try{
            return ResponseEntity.ok(instructorService.getAllMessage(idLocation));
        } catch(IllegalStateException e) {
            return new ResponseEntity<>(MessageResponse.builder().error(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * L'endPoint è a uso esclusivo dell'admin e serve per eliminare un messaggio, da notare che a scascata verranno
     * eliminati tutti i commenti che ad esso fanno riferimento
     * @param request
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/delete/message")
    public ResponseEntity<?> deleteMessageByInstructor(@RequestBody MessageDTO request) {
        try {
            return ResponseEntity.ok(instructorService.deleteMessage(request));
        }catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * L'endPoint è a uso esclusivo dell'admin e serve per eliminare uno o più commenti di un messaggio
     * Ha in ingresso una request generata come DTO e solleva eccezioni nel caso in cui nel service vengano
     * rilevati errori di inconsistenza
     * @param request
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("delete/comments")
    public ResponseEntity<?> deleteCommentFromMessageByInstructor(@RequestBody MessageDTO request) {
        try {
            return ResponseEntity.ok(instructorService.deleteComments(request));
        }catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
