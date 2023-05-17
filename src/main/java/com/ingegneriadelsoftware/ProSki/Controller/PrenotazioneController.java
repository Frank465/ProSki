package com.ingegneriadelsoftware.ProSki.Controller;

import com.ingegneriadelsoftware.ProSki.DTO.DTOManager;
import com.ingegneriadelsoftware.ProSki.DTO.Request.PrenotazioneRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.AttrezzaturaDisponibileResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Response.PrenotazioneResponse;
import com.ingegneriadelsoftware.ProSki.Model.Prenotazione;
import com.ingegneriadelsoftware.ProSki.Service.PrenotazioneService;
import com.ingegneriadelsoftware.ProSki.Service.RifornitoreService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;

@RestController
@RequestMapping("/api/v1/prenotazione")
@RequiredArgsConstructor
public class PrenotazioneController {

    private final RifornitoreService rifornitoreService;
    private final PrenotazioneService prenotazioneService;

    @PostMapping("/create")
    public ResponseEntity<?> createPrenotazione(@Valid @RequestBody PrenotazioneRequest request, HttpServletRequest servletRequest) {
        try{
            PrenotazioneResponse prenotazioneResponse = DTOManager.toPrenotazioneResponseByPrenotazione(prenotazioneService.creaPrenotazione(request, servletRequest));
            return ResponseEntity.ok(prenotazioneResponse);
        }catch(IllegalStateException | DateTimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getAttrezzature/{id}")
    public ResponseEntity<?> getAttrezzatureDisponibiliByRifornitore(@PathVariable Integer id) {
        try{
            return ResponseEntity.ok(rifornitoreService.getAttrezzaturaDisponibile(id));
        }catch (IllegalStateException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
