package com.ingegneriadelsoftware.ProSki.Controller;

import com.ingegneriadelsoftware.ProSki.DTO.AuthenticationRequest;
import com.ingegneriadelsoftware.ProSki.DTO.AuthenticationResponse;
import com.ingegneriadelsoftware.ProSki.DTO.RegistrazioneRequest;
import com.ingegneriadelsoftware.ProSki.DTO.RegistrazioneResponse;
import com.ingegneriadelsoftware.ProSki.Service.ProfiloService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profilo")
@RequiredArgsConstructor
public class Profilo {
    private final ProfiloService profiloService;

    @PostMapping("/register")
    public ResponseEntity<RegistrazioneResponse> registrazione(@RequestBody RegistrazioneRequest request) {
        RegistrazioneResponse response = profiloService.registrazione(request);
        if(response.getMessage() == null)
            return ResponseEntity.ok(response);
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Viene confermato il token che indica la corretta iscrizione al sito, dopo l'invio della mail
     * @param token
     * @return
     */
    @GetMapping("/confirm")
    public ResponseEntity<String> confermaRegistrazione(@RequestParam("token") String token) {
        String message = profiloService.confermaToken(token);
        if(message.equals("confirmed"))
            return ResponseEntity.ok(message);
        return ResponseEntity.internalServerError().body(message);
    }

    @PostMapping("/authentication")
    public ResponseEntity<AuthenticationResponse> authenticateRequest(@RequestBody AuthenticationRequest request) {
         AuthenticationResponse response = profiloService.authentication(request);
         if(response.getMessage() == null)
             return ResponseEntity.ok(response);
         return ResponseEntity.badRequest().body(response);
    }
}
