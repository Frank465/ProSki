package com.ingegneriadelsoftware.ProSki.Controller;

import com.ingegneriadelsoftware.ProSki.DTO.Request.AuthenticationRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Request.RegistrazioneRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.AuthenticationResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Response.RegistrazioneResponse;
import com.ingegneriadelsoftware.ProSki.Service.ProfiloService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profilo")
@RequiredArgsConstructor
public class ProfiloController {
    private final ProfiloService profiloService;

    @PostMapping("/register")
    public ResponseEntity<RegistrazioneResponse> registrazione(@Valid @RequestBody RegistrazioneRequest request) {

        try {
            return ResponseEntity.ok(profiloService.registrazione(request));
        }catch(IllegalStateException e){
            return new ResponseEntity<>(
                    RegistrazioneResponse
                            .builder()
                            .message(e.getMessage())
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Viene confermato il token che indica la corretta iscrizione al sito, dopo l'invio della mail
     * @param token
     * @return
     */
    @GetMapping("/confirm")
    public ResponseEntity<?> confermaRegistrazione(@RequestParam("token") String token) {
        try {
            profiloService.confermaToken(token);
        }catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.ok("Confirm");
    }

    /**
     * Login Utente
     * @param request
     * @return
     */
    @PostMapping("/authentication")
    public ResponseEntity<AuthenticationResponse> authenticateRequest(@Valid @RequestBody AuthenticationRequest request) {
        try{
            return ResponseEntity.ok(profiloService.authentication(request));
        }catch (AuthenticationException unf){
            return new ResponseEntity<>(
                    AuthenticationResponse
                            .builder()
                            .message(unf.getMessage())
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
