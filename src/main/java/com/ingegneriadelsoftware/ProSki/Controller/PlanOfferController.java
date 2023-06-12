package com.ingegneriadelsoftware.ProSki.Controller;


import com.ingegneriadelsoftware.ProSki.DTO.DTOManager;
import com.ingegneriadelsoftware.ProSki.DTO.Request.OfferRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.PlanRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.OfferResponse;
import com.ingegneriadelsoftware.ProSki.Service.OfferService;
import com.ingegneriadelsoftware.ProSki.Service.PlanService;
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
@RequestMapping("/api/v1/planoffer")
@RequiredArgsConstructor
public class PlanOfferController {

    private final PlanService planService;
    private final OfferService offerService;

    @PreAuthorize("hasRole('RUOLO_ADMIN')")
    @PostMapping("/create/plan")
    public ResponseEntity<String> createPlan(@Valid @RequestBody PlanRequest request) {
        try{
            return ResponseEntity.ok(planService.createPlan(request.getPlanName()));
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PreAuthorize("hasRole('RUOLO_ADMIN')")
    @PostMapping("/create/offer")
    public ResponseEntity<OfferResponse> createOffer(@Valid @RequestBody OfferRequest request) {
        try{
            return ResponseEntity.ok(DTOManager.toOfferResponseByOffer(offerService.createOffer(request)));
        } catch(IllegalStateException ex) {
            return new ResponseEntity<>(OfferResponse.builder().message(ex.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }
}
