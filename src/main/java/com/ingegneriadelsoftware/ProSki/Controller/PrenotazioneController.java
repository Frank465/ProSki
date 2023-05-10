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
    private final DTOManager dtoManager;

    @PostMapping("/create")
    public ResponseEntity<?> createPrenotazione(@Valid @RequestBody PrenotazioneRequest request, HttpServletRequest servletRequest) {
        Prenotazione prenotazione = dtoManager.getPrenotazioneByPrenotazioneRequest(request, servletRequest);
        try{
            PrenotazioneResponse prenotazioneResponse = dtoManager.toPrenotazioneResponseByPrenotazione(prenotazioneService.creaPrenotazione(prenotazione));
            return ResponseEntity.ok(prenotazioneResponse);
        }catch(IllegalStateException | DateTimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getAttrezzature/{id}")
    public ResponseEntity<?> getAttrezzatureDisponibiliByRifornitore(@PathVariable Integer idRifornitore) {
        try{
            return ResponseEntity.ok(rifornitoreService.getAttrezzaturaDisponibile(idRifornitore));
        }catch (IllegalStateException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
