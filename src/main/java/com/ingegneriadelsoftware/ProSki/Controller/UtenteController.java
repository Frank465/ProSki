package com.ingegneriadelsoftware.ProSki.Controller;

import com.ingegneriadelsoftware.ProSki.DTO.DTOManager;
import com.ingegneriadelsoftware.ProSki.DTO.Request.IscrizioneLezioneRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.LezioneResponse;
import com.ingegneriadelsoftware.ProSki.Model.Lezione;
import com.ingegneriadelsoftware.ProSki.Service.LezioneService;
import com.ingegneriadelsoftware.ProSki.Service.UtenteService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/utente")
@RequiredArgsConstructor
public class UtenteController {

    private final UtenteService utenteService;
    private final LezioneService lezioneService;

    @GetMapping("/lezioni")
    public ResponseEntity<?> getAllLezioneByUtente(HttpServletRequest servletRequest) {
        List<LezioneResponse> lezioniReponse = new ArrayList<>();
        try{
            List<Lezione> lezioni = utenteService.getLezioniByUtente(servletRequest);
            lezioni.forEach(cur-> {
                lezioniReponse.add(DTOManager.toLezioneResponseByLezione(cur));
            });
            return ResponseEntity.ok(lezioniReponse);
        }catch(EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/iscrizione/lezione")
    public ResponseEntity<?> iscrizioneUtenteLezione(@RequestBody  List<IscrizioneLezioneRequest> request, HttpServletRequest servletRequest) {
        try{
            return ResponseEntity.ok(utenteService.iscrizioneLezioni(request, servletRequest));
        }catch(EntityNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/tutte/lezioni")
    public ResponseEntity<?> getLezioni() {
        try{
            return ResponseEntity.ok(lezioneService.getListLessons());
        }catch(EntityNotFoundException ex) {
            return new ResponseEntity<>("Non ci sono lezioni disponibili", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
