package com.ingegneriadelsoftware.ProSki.Controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ingegneriadelsoftware.ProSki.DTO.DTOManager;
import com.ingegneriadelsoftware.ProSki.DTO.Request.*;
import com.ingegneriadelsoftware.ProSki.Model.Lezione;
import com.ingegneriadelsoftware.ProSki.Model.Localita;
import com.ingegneriadelsoftware.ProSki.Model.Maestro;
import com.ingegneriadelsoftware.ProSki.Model.Rifornitore;
import com.ingegneriadelsoftware.ProSki.Repository.UtenteRepository;
import com.ingegneriadelsoftware.ProSki.Service.LezioneService;
import com.ingegneriadelsoftware.ProSki.Service.LocalitaService;
import com.ingegneriadelsoftware.ProSki.Service.MaestroService;
import com.ingegneriadelsoftware.ProSki.Service.RifornitoreService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MaestroService maestroService;
    private final UtenteRepository utenteRepository;
    private final LocalitaService localitaService;
    private final RifornitoreService rifornitoreService;
    private final LezioneService lezioneService;

    @GetMapping
    public String getMessage(){
        return "ciao Admin";
    }

    @PostMapping("/create/localita")
    public ResponseEntity<?> createLocalita(@Valid @RequestBody LocalitaRequest request) {
        Localita localita = DTOManager.getLocalitaByLocalitaRequest(request);
        try{
            return ResponseEntity.ok(localitaService.creaLocalita(localita));
        }catch(IllegalStateException enf){
            return new ResponseEntity<>(enf.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/create/maestro")
    public ResponseEntity<?> createMaestro(@Valid @RequestBody MaestroRequest request) {
        Maestro maestro = DTOManager.getMaestroByMaestroRequest(request);
        try{
            return ResponseEntity.ok(maestroService.inserisciMaestro(maestro, request.getLocalita()));
        }catch(IllegalStateException | EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(),HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/create/rifornitore")
    public ResponseEntity<?> createRifornitore(@Valid @RequestBody RifornitoreRequest request) {
        Rifornitore rifornitore = DTOManager.getRifornitoreByRifornitoreRequest(request);
        try{
            return ResponseEntity.ok(rifornitoreService.inserisciRifornitore(rifornitore));
        }catch(IllegalStateException | EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(),HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/insert/attrezzaturaRifornitore")
    public ResponseEntity<?> updateAttrezzatureRifornitore(@Valid @RequestBody AttrezzatureRifornitoreRequest request) {
        try {
            return ResponseEntity.ok(rifornitoreService.createAttrezzature(request));
        }catch(IllegalStateException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/utente")
    public void deleteUtente(@RequestBody String email) {
        utenteRepository.deleteByEmail(email);
    }

    @PostMapping("/create/lezione")
    public ResponseEntity<?> createLezione(@Valid @RequestBody LezioneRequest request) {
        try{
            return ResponseEntity.ok(DTOManager.toLezioneResponseByLezione(lezioneService.createLezione(request)));
        } catch (IllegalStateException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
