package com.ingegneriadelsoftware.ProSki.Controller;

import com.ingegneriadelsoftware.ProSki.DTO.Request.*;
import com.ingegneriadelsoftware.ProSki.DTO.Response.EquipmentAvailableResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Response.MessageResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Utils.MessageDTO;
import com.ingegneriadelsoftware.ProSki.Service.VendorService;
import com.ingegneriadelsoftware.ProSki.Utils.Utils;
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
@RequestMapping("/api/v1/vendor")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;


    /**
     * L'endPoint consente al solo Admin di creare dei Rifornitori a partire da una request(DTO) nella quale sono presente
     * le informazioni del nuovo rifornitore, che vengono validate. La request viene passata al service il quale, nel caso di
     * corretto inserimento, risponde con un messaggio di notifica che viene inserito nella ResponseEntity.
     * Le eccezioni possono essere dovute al già avvenuto inserimento di quel rifornitore
     * @param request
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createVendor(@Valid @RequestBody VendorRequest request) {
        try{
            return ResponseEntity.ok(vendorService.insertVendor(request));
        }catch(IllegalStateException | EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * L'endPoint è utilizzabile solo dall'amministratore e permette l'inserimento, per un rifornitore, di un insieme di
     * attrezzature. Le informazioni sono contenute nella request(DTO) e vengono inizialmente validate.
     * La request viene passata al metodo del service che ritorna, nel caso di corretto inserimento una stringa di notifica
     * un errore che si può manifestare è che il rifornitore indicato non esiste
     * @param request
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/insert/equipment")
    public ResponseEntity<?> updateVendorEquipment(@Valid @RequestBody VendorEquipmentRequest request) {
        try {
            return ResponseEntity.ok(vendorService.createEquipment(request));
        }catch(IllegalStateException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Questo endPoint viene chiamato ogni volta che si vuole creare un messaggio nel form di un rifornitore.
     * Accetta in ingresso una request composta come un DTO prende httprequest necessario per capire chi è l'utente che sta
     * scrivendo il messaggio. Solleva eccezioni nel caso di mancanza d'informazioni o dati passanti non esistenti nel db
     * @param request
     * @param httpRequest
     * @return
     */
    @PostMapping("/create/message")
    public ResponseEntity<?> createMessageToVendor(@Valid @RequestBody MessageRequest request, HttpServletRequest httpRequest) {
        try {
            return ResponseEntity.ok(vendorService.createMessage(request, httpRequest));
        } catch(EntityNotFoundException | IllegalStateException e) {
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
    public ResponseEntity<?> createCommentToVendorMessage(@Valid @RequestBody CommentRequest request, HttpServletRequest httpRequest) {
        try {
            return ResponseEntity.ok(vendorService.createCommentToMessage(request, httpRequest));
        } catch(EntityNotFoundException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * EndPoint da chiamare prima di creare una prenotazione, per prendere la lista delle attrezzature del rifornitore
     * @param request
     * @return
     */
    @GetMapping("/getEquipmentAvailable/vendor")
    public ResponseEntity<EquipmentAvailableResponse> getEquipmentAvailableByVendor(@Valid @RequestBody EquipementVendorAvailable request) {
        try{
            return ResponseEntity.ok(vendorService.getEquipmentAvailableForDate(request.getVendorEmail(), Utils.formatterData(request.getStartDate()), Utils.formatterData(request.getEndDate())));
        }catch (IllegalStateException | DateTimeException ex) {
            return new ResponseEntity<>(EquipmentAvailableResponse.builder().message(ex.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * L'endPoint serve per ritornare tutti i messaggi e i commenti che sono stati scritti per un determinato rifornitore
     * Utilizzato dagli utenti per poter interagire e admin per poter controllare che non ci siano messaggi indiscreti
     * @param idLocation
     * @return
     */
    @GetMapping("/get/all/message/{id_vendor}")
    public ResponseEntity<MessageResponse> getAllMessagesByVendor(@PathVariable("id_vendor") Integer idLocation) {
        try{
            return ResponseEntity.ok(vendorService.getAllMessage(idLocation));
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
    public ResponseEntity<?> deleteMessageByVendor(@RequestBody MessageDTO request) {
        try {
            return ResponseEntity.ok(vendorService.deleteMessage(request));
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
    public ResponseEntity<?> deleteMessagesByVendor(@RequestBody MessageDTO request) {
        try {
            return ResponseEntity.ok(vendorService.deleteComments(request));
        }catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}