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
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profilo")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

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
            profileService.confirmToken(token);
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
            return ResponseEntity.ok(profileService.authentication(request));
        }catch (AuthenticationException unf){
            return new ResponseEntity<>(AuthenticationResponse.builder().message(unf.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }
}
