package com.ingegneriadelsoftware.ProSki.Controller;

import com.ingegneriadelsoftware.ProSki.DTO.Request.*;
import com.ingegneriadelsoftware.ProSki.DTO.Response.EquipmentAvailableResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Response.MessageResponse;
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


    @PreAuthorize("hasRole('RUOLO_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createVendor(@Valid @RequestBody VendorRequest request) {
        try{
            return ResponseEntity.ok(vendorService.insertVendor(request));
        }catch(IllegalStateException | EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('RUOLO_ADMIN')")
    @PostMapping("/insert/equipment")
    public ResponseEntity<?> updateVendorEquipment(@Valid @RequestBody VendorEquipmentRequest request) {
        try {
            return ResponseEntity.ok(vendorService.createEquipment(request));
        }catch(IllegalStateException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/create/message")
    public ResponseEntity<?> createMessageToVendor(@Valid @RequestBody MessageRequest request, HttpServletRequest httpRequest) {
        try {
            return ResponseEntity.ok(vendorService.createMessage(request, httpRequest));
        } catch(EntityNotFoundException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

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

    @GetMapping("/get/all/message/{id_vendor}")
    public ResponseEntity<MessageResponse> getAllMessagesByVendor(@PathVariable("id_vendor") Integer idLocation) {
        try{
            return ResponseEntity.ok(vendorService.getAllMessage(idLocation));
        } catch(IllegalStateException e) {
            return new ResponseEntity<>(MessageResponse.builder().error(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }

    }

}
