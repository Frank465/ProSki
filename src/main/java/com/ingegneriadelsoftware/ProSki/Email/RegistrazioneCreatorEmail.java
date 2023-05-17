package com.ingegneriadelsoftware.ProSki.Email;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegistrazioneCreatorEmail extends CreatorEmail {
    private final String name;
    private final String link;

    public BuildEmail createEmail() {
        return new BuildEmailRegistrazione(name, link);
    }
}
