package com.ingegneriadelsoftware.ProSki.Controller;

import com.ingegneriadelsoftware.ProSki.DTO.DTOManager;
import com.ingegneriadelsoftware.ProSki.DTO.Request.ReservationRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.EquipmentAvailableResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Response.ReservationResponse;
import com.ingegneriadelsoftware.ProSki.Service.ReservationService;
import com.ingegneriadelsoftware.ProSki.Service.VendorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;

@RestController
@RequestMapping("/api/v1/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final VendorService vendorService;
    private final ReservationService reservationService;

    @PostMapping("/create")
    public ResponseEntity<?> createReservation(@Valid @RequestBody ReservationRequest request, HttpServletRequest servletRequest) {
        try{
            ReservationResponse reservationResponse = DTOManager.toReservationResponseByReservation(reservationService.createReservation(request, servletRequest));
            return ResponseEntity.ok(reservationResponse);
        }catch(IllegalStateException | DateTimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * EndPoint da chiamare prima di creare una prenotazione, per prendere la lista delle attrezzature del rifornitore
     * @param idVendor
     * @return
     */
    @GetMapping("/getEquipment/vendor/{idVendor}")
    public ResponseEntity<EquipmentAvailableResponse> getEquipmentAvailableByVendor(@PathVariable Integer idVendor) {
        try{
            return ResponseEntity.ok(vendorService.getEquipmentAvailable(idVendor));
        }catch (IllegalStateException ex) {
            return new ResponseEntity<>(EquipmentAvailableResponse.builder().message(ex.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

}
