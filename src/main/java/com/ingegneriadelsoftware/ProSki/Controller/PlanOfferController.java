package com.ingegneriadelsoftware.ProSki.Controller;


import com.ingegneriadelsoftware.ProSki.DTO.DTOManager;
import com.ingegneriadelsoftware.ProSki.DTO.Request.OfferRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.PlanRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.OfferResponse;
import com.ingegneriadelsoftware.ProSki.Service.PlaneOfferService;
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
@RequestMapping("/api/v1/planOffer")
@RequiredArgsConstructor
public class PlanOfferController {

    private final PlaneOfferService planeOfferService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/create/plan")
    public ResponseEntity<String> createPlan(@Valid @RequestBody PlanRequest request) {
        try{
            return ResponseEntity.ok(planeOfferService.createPlan(request.getPlanName()));
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/create/offer")
    public ResponseEntity<OfferResponse> createOffer(@Valid @RequestBody OfferRequest request) {
        try{
            return ResponseEntity.ok(DTOManager.toOfferResponseByOffer(planeOfferService.createOffer(request)));
        } catch(IllegalStateException ex) {
            return new ResponseEntity<>(OfferResponse.builder().message(ex.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }
}
