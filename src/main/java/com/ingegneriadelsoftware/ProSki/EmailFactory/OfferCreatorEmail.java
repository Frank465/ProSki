package com.ingegneriadelsoftware.ProSki.EmailFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OfferCreatorEmail extends CreatorEmail {
    private final String name;
    private final String date;

    public BuildEmail createEmail() {
        return new BuildOfferEmail(name, date);
    }
}