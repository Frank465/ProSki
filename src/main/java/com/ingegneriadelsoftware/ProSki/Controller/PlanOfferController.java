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

    /**
     * L'endPoint è ad uso esclusivo dell'utente admin e permette la creazione di un piano.
     * Prende in input una request (DTO) della quale un campo viene mandato al service per la creazione del piano.
     * Il tipo di ritorno è una stringa, se tutto è andato bene, che notifica la corretta creazione
     * Le eccezioni che possono essere sollevate riguardano i parametri che non sono validi già sono presenti nel DB
     * @param request
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/create/plan")
    public ResponseEntity<String> createPlan(@Valid @RequestBody PlanRequest request) {
        try{
            return ResponseEntity.ok(planeOfferService.createPlan(request.getPlanName()));
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    /**
     * L'endPoint è ad uso esclusivo dell'utente admin e permette la creazione di un'offerta.
     * Prende in input una request (DTO) che viene mandata al service per la creazione dell'offerta.
     * Nella request è comunque presente il piano a cui l'offerta fa riferimento
     * Il tipo di ritorno è un'offerta che viene mappata opportunamente a OfferResponse e inserire nel corpo della
     * risposta. Le eccezioni che possono essere sollevate riguardano i parametri che non sono validi
     * (le date errate) o sono già sono presenti nel DB
     * @param request
     * @return
     */
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
