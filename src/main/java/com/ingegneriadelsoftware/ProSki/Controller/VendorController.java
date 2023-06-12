package com.ingegneriadelsoftware.ProSki.Controller;

import com.ingegneriadelsoftware.ProSki.DTO.Request.VendorEquipmentRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.VendorRequest;
import com.ingegneriadelsoftware.ProSki.Service.VendorService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
