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
        return ResponseEntity.ok(profiloService.registrazione(request));
    }

    /**
     * Viene confermato il token che indica la corretta iscrizione al sito, dopo l'invio della mail
     * @param token
     * @return
     */
    @GetMapping("/confirm")
    public ResponseEntity<String> confermaRegistrazione(@RequestParam("token") String token) {
        return ResponseEntity.ok(profiloService.confermaToken(token));
    }

    @PostMapping("/authentication")
    public ResponseEntity<AuthenticationResponse> authenticateRequest(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(profiloService.authentication(request));
    }

}
