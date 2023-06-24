package com.ingegneriadelsoftware.ProSki.Controller;

import com.ingegneriadelsoftware.ProSki.DTO.DTOManager;
import com.ingegneriadelsoftware.ProSki.DTO.Request.CardSkipassRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.CommentRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.LocationRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.MessageRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.LocationResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Response.MessageResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Utils.MessageDTO;
import com.ingegneriadelsoftware.ProSki.Service.LocationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;

@RestController
@RequestMapping("/api/v1/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    /**
     * L'endpoint è ad uso esclusivo dell'admin e richiede una LocationRequest(DTO) come input.
     * Quindi richiama il service indicato per creare la location passando la request.
     * Se l'inserimento è stato svolto correttamente il metodo ritorna la località che viene mappata
     * su un oggetto di tipo LocalitaResponse e poi inserita nel corpo della Response Entity.
     * Il metodo solleva eccezioni nel caso di valori sbalgliati o incosistenze, salvando il messaggio dell'errore
     * in oggetto LocationResponse contenuto nella risposta
     * @param request
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<LocationResponse> createLocation(@Valid @RequestBody LocationRequest request) {
        try{
            return ResponseEntity.ok(DTOManager.toLocationResponseByLocation(locationService.createLocation(request)));
        }catch(IllegalStateException | DateTimeException enf){
            return new ResponseEntity<>(LocationResponse.builder().message(enf.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * L'endPoint è accessibile a tutti e ritorna la lista di tutte le località.
     * Solleva eccezioni nel caso in cui non esistano località
     * @return
     */
    @GetMapping("getAll")
    public ResponseEntity<?> getAllLocation() {
        try {
            return ResponseEntity.ok(locationService.getAllLocation());
        }catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * L'endpoint è riservato al solo utilizzo dell'admin e permette la creazione di skipass prendendo come ingresso
     * un request(DTO) che contiene le informazioni necessarie. Che vengono validate @Valid (Jakarta).
     * Viene chiamato il metodo createSkipass che se l'operazione va a buon termine ritorna una stringa di avvenuto
     * inserimento. Solleva eccezione nel caso di errore di formati, dati non validi o presenza dello skipass inserito
     * @param request
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/create/skipass")
    public ResponseEntity<String> createSkipass(@Valid @RequestBody CardSkipassRequest request) {
        try {
            return ResponseEntity.ok(locationService.createSkipass(request));
        }catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Questo endPoint viene chiamato ogni volta che si vuole creare un messaggio nel form di una località.
     * Accetta in ingresso una request composta come un DTO prende httprequest necessario per capire chi è l'utente che sta
     * scrivendo il messaggio. Solleva eccezioni nel caso di mancanza di informazioni o dati passanti non esistenti nel db
     * @param request
     * @param httpRequest
     * @return
     */
    @PostMapping("create/message")
    public ResponseEntity<String> createMessageToLocation(@Valid @RequestBody MessageRequest request, HttpServletRequest httpRequest) {
        try {
            return ResponseEntity.ok(locationService.createMessage(request, httpRequest));
        } catch(IllegalStateException e) {
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
    public ResponseEntity<?> createCommentToLocationMessage(@Valid @RequestBody CommentRequest request, HttpServletRequest httpRequest) {
        try {
            return ResponseEntity.ok(locationService.createCommentToMessage(request, httpRequest));
        } catch(IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * L'endPoint serve per ritornare tutti i messaggi e i commenti che sono stati scritti per una determinata località
     * Utilizzato dagli utenti per poter interagire e admin per poter controllare che non ci siano messaggi indiscreti
     * @param idLocation
     * @return
     */
    @GetMapping("/get/all/message/{id_location}")
    public ResponseEntity<MessageResponse> gelAllMessageByLocation(@PathVariable("id_location") Integer idLocation) {
        try{
            return ResponseEntity.ok(locationService.getAllMessage(idLocation));
        } catch(IllegalStateException e) {
            return new ResponseEntity<>(MessageResponse.builder().error(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }

    }

    /**
     * L'endPoint è a uso esclusivo dell'admin e serve per eliminare un messaggio, da notare che a cascata verranno
     * eliminati tutti i commenti che ad esso fanno riferimento
     * @param request
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/delete/message")
    public ResponseEntity<?> deleteMessageByLocation(@RequestBody MessageDTO request) {
        try {
            return ResponseEntity.ok(locationService.deleteMessage(request));
        }catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * L'endPoint è a uso esclusivo dell'admin e serve per eliminare uno o più commenti di un messaggio
     * Ha in ingresso una request generata come DTO e solleva eccezioni nel caso in cui nel service vengano
     * rilevati errori d'inconsistenza
     * @param request
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("delete/comments")
    public ResponseEntity<?> deleteMessagesByLocation(@RequestBody MessageDTO request) {
        try {
            return ResponseEntity.ok(locationService.deleteComments(request));
        }catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
