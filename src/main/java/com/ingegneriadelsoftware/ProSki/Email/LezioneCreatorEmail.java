package com.ingegneriadelsoftware.ProSki.Email;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LezioneCreatorEmail extends CreatorEmail {
    private final String nameMaestro;
    private final String nameUtente;
    private final String specialita;

    public BuildEmail createEmail() {
        return new BuildEmailLezione(nameMaestro, nameUtente, specialita);
    }
}
