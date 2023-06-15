package com.ingegneriadelsoftware.ProSki.EmailFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegisterCreatorEmail extends CreatorEmail {
    private final String name;
    private final String link;

    public BuildEmail createEmail() {
        return new BuildRegisterEmail(name, link);
    }
}
