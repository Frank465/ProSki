package com.ingegneriadelsoftware.ProSki.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class Admin {

    @GetMapping
    public String getMessage(){
        return "ciao Admin";
    }
}
