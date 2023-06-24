package com.ingegneriadelsoftware.ProSki.Controller;

import com.ingegneriadelsoftware.ProSki.DTO.Request.AuthenticationRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.RegisterRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.AuthenticationResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Response.RegisterResponse;
import com.ingegneriadelsoftware.ProSki.Service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profilo")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    /**
     * L'endPoint a cui tutti hanno accesso permette la registrazione di un utente.
     * Prende in ingresso una request(DTO) a cui vengono validati i valori (email, password, gender)
     * La request viene passata al service che crea e ritorna un DTO RegisterResponse che viene inserito nella risposta.
     * Il metodo solleva eccezioni nel caso di utenti già presenti oppure valori richiesti incosistenti
     * @param request
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(profileService.register(request));
        }catch(IllegalStateException e){
            return new ResponseEntity<>(RegisterResponse.builder().message(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Viene confermato il token che indica la corretta iscrizione al sito, dopo l'invio della mail
     * @param token
     * @return
     */
    @GetMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam("token") String token) {
        try {
            return ResponseEntity.ok(profileService.confirmToken(token));
        }catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Login Utente attraverso un DTO AuthenticationRequest in cui sono contenuti email e password
     * L'autenticazione avviene attraverso Spring Security, quindi se tutto va a buon fine all'utente viene assegnato un
     * token dalla durata di 1 giorno. Per ogni chiamata alle altre API l'utente utilizzerà quel token.
     * @param request
     * @return
     */
    @PostMapping("/authentication")
    public ResponseEntity<AuthenticationResponse> authenticateRequest(@Valid @RequestBody AuthenticationRequest request) {
        try{
            return ResponseEntity.ok(profileService.authentication(request));
        }catch (IllegalStateException e){
            return new ResponseEntity<>(AuthenticationResponse.builder().message(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }
}
