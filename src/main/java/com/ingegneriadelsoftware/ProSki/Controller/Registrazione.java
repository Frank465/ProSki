package com.ingegneriadelsoftware.ProSki.Controller;

import com.ingegneriadelsoftware.ProSki.DTO.RegistrazioneRequest;
import com.ingegneriadelsoftware.ProSki.Service.RegistrazioneService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/registrazione")
@RequiredArgsConstructor
public class Registrazione {

    private final RegistrazioneService registrazioneService;

    @PostMapping
    public String registrazione(@RequestBody RegistrazioneRequest request) {
        return registrazioneService.registrazione(request);
    }

    @GetMapping("/confirm")
    public String confermation(@RequestParam("token") String token) {
        return registrazioneService.confermaToken(token);
    }

}
