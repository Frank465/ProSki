package com.ingegneriadelsoftware.ProSki.Email;

import org.springframework.stereotype.Component;


/**
 * Classe astratta che
 */
@Component
public abstract class CreatorEmail {

    public abstract BuildEmail createEmail();

    public String render() {
        // Chiama il factory method per creare un oggetto di tipo BuildEmail
        BuildEmail email = createEmail();
        // Ora si usa il BuildEmail.
        return email.render();
    }
}
